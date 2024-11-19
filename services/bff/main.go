package main

import (
	"bytes"
	"encoding/json"
	"fmt"
	"net/http"
	"os"
	"strings"
	"time"

	"github.com/gin-contrib/cors"
	"github.com/gin-contrib/logger"
	"github.com/gin-gonic/gin"
	"github.com/rs/zerolog"
	"github.com/rs/zerolog/log"
)

type Date struct {
	time.Time
}

func (ct *Date) UnmarshalJSON(b []byte) (err error) {
	s := strings.Trim(string(b), "\"")
	if s == "null" {
		ct.Time = time.Time{}
		return
	}
	ct.Time, err = time.Parse("2006-01-02", s)
	return
}

type AuthRequest struct {
	ClientID     string `json:"clientId"`
	ClientSecret string `json:"clientSecret"`
}

type AuthResponse struct {
	AccessToken string  `json:"accessToken"`
	ExpiresIn   float64 `json:"expiresIn"`
}

type InstallmentDto struct {
	Value             float64 `json:"value,omitempty"`
	InstallmentNumber int     `json:"installmentNumber,omitempty"`
	DueDate           Date    `json:"dueDate,omitempty"`
}

type RateDto struct {
	Id          int64   `json:"id,omitempty"`
	Name        string  `json:"name"`
	Description string  `json:"description,omitempty"`
	Value       float64 `json:"value,omitempty"`
}

type LoanDto struct {
	Id                 int64            `json:"id,omitempty"`
	Name               string           `json:"name"`
	Description        string           `json:"description,omitempty"`
	Value              float64          `json:"value,omitempty"`
	InstallmentsNumber int              `json:"installmentsNumber,omitempty"`
	Installments       []InstallmentDto `json:"installments,omitempty"`
	StartDate          Date             `json:"startDate,omitempty"`
	Rate               RateDto          `json:"rate,omitempty"`
}

func fetchRates(apiUrl, token string) ([]RateDto, error) {
	req, err := http.NewRequest("GET", fmt.Sprintf("%s/api/v1/rates", apiUrl), nil)
	if err != nil {
		return nil, err
	}
	req.Header.Set("Authorization", fmt.Sprintf("Bearer %s", token))

	client := &http.Client{}
	resp, err := client.Do(req)
	if err != nil {
		return nil, err
	}
	defer resp.Body.Close()

	if resp.StatusCode != http.StatusOK {
		return nil, fmt.Errorf("failed to fetch rates, status code: %d", resp.StatusCode)
	}

	var rates []RateDto
	err = json.NewDecoder(resp.Body).Decode(&rates)
	if err != nil {
		return nil, err
	}

	return rates, nil
}

func fetchLoans(apiUrl, token string) ([]LoanDto, error) {
	req, err := http.NewRequest("GET", fmt.Sprintf("%s/api/v1/loans", apiUrl), nil)
	if err != nil {
		return nil, err
	}
	req.Header.Set("Authorization", fmt.Sprintf("Bearer %s", token))

	client := &http.Client{}
	resp, err := client.Do(req)
	if err != nil {
		return nil, err
	}
	defer resp.Body.Close()

	if resp.StatusCode != http.StatusOK {
		return nil, fmt.Errorf("failed to fetch loans, status code: %d", resp.StatusCode)
	}

	var loans []LoanDto
	err = json.NewDecoder(resp.Body).Decode(&loans)
	if err != nil {
		return nil, err
	}

	return loans, nil
}

func fetchLoanById(apiUrl string, loanId int64, token string) (LoanDto, error) {
	req, err := http.NewRequest("GET", fmt.Sprintf("%s/api/v1/loans/%s", apiUrl, loanId), nil)
	if err != nil {
		return LoanDto{}, err
	}
	req.Header.Set("Authorization", fmt.Sprintf("Bearer %s", token))

	client := &http.Client{}
	resp, err := client.Do(req)
	if err != nil {
		return LoanDto{}, err
	}
	defer resp.Body.Close()

	if resp.StatusCode != http.StatusOK {
		return LoanDto{}, fmt.Errorf("failed to fetch loan by ID, status code: %d", resp.StatusCode)
	}

	var loan LoanDto
	err = json.NewDecoder(resp.Body).Decode(&loan)
	if err != nil {
		return LoanDto{}, err
	}

	return loan, nil
}

func saveLoan(apiUrl string, loan LoanDto, token string) error {
	body, err := json.Marshal(loan)
	if err != nil {
		return err
	}

	req, err := http.NewRequest("POST", fmt.Sprintf("%s/api/v1/loans", apiUrl), bytes.NewBuffer(body))
	if err != nil {
		return err
	}
	req.Header.Set("Content-Type", "application/json")
	req.Header.Set("Authorization", fmt.Sprintf("Bearer %s", token))

	client := &http.Client{}
	resp, err := client.Do(req)
	if err != nil {
		return err
	}
	defer resp.Body.Close()

	return nil
}

func getClientToken(apiUrl string, clientID, clientSecret string) (string, error) {
	url := fmt.Sprintf("%s/api/auth/token", apiUrl)

	authRequest := AuthRequest{
		ClientID:     clientID,
		ClientSecret: clientSecret,
	}

	requestBody, err := json.Marshal(authRequest)
	if err != nil {
		return "", err
	}

	resp, err := http.Post(url, "application/json", bytes.NewBuffer(requestBody))
	if err != nil {
		return "", err
	}
	defer resp.Body.Close()

	if resp.StatusCode != http.StatusOK {
		return "", fmt.Errorf("failed to get client token, status code: %d", resp.StatusCode)
	}

	var authResponse AuthResponse
	err = json.NewDecoder(resp.Body).Decode(&authResponse)
	if err != nil {
		return "", err
	}

	return authResponse.AccessToken, nil
}

func main() {
	port := os.Getenv("PORT")
	if port == "" {
		port = "8085" // Default port if not specified
	}

	apiUrl := os.Getenv("API_URL")
	if apiUrl == "" {
		apiUrl = "http://localhost:8080" // Default port if not specified
	}

	clientId := os.Getenv("CLIENT_ID")
	clientSecret := os.Getenv("CLIENT_SECRET")

	if clientId == "" || clientSecret == "" {
		log.Fatal().Msg("CLIENT_ID and CLIENT_SECRET environment variables must be set")
		os.Exit(1)
	}

	token, err := getClientToken(apiUrl, clientId, clientSecret)
	if err != nil {
		log.Fatal().Msg(err.Error())
		os.Exit(1)
	}

	r := gin.New()
	r.Use(cors.New(cors.Config{
		AllowOrigins:  []string{"*"},
		AllowMethods:  []string{"*"},
		AllowHeaders:  []string{"*"},
		ExposeHeaders: []string{"*"},
		MaxAge:        12 * time.Hour,
	}))

	r.Use(logger.SetLogger(
		logger.WithLogger(func(_ *gin.Context, l zerolog.Logger) zerolog.Logger {
			return l.Output(gin.DefaultWriter).With().Logger()
		}),
	))

	r.GET("/api/rates", func(c *gin.Context) {
		rates, err := fetchRates(apiUrl, token)
		if err != nil {
			c.JSON(http.StatusInternalServerError, gin.H{
				"error": err.Error(),
			})
			return
		}
		c.JSON(http.StatusOK, gin.H{
			"data": rates,
		})
	})

	r.GET("/api/loans", func(c *gin.Context) {
		loans, err := fetchLoans(apiUrl, token)
		if err != nil {
			c.JSON(http.StatusInternalServerError, gin.H{
				"error": err.Error(),
			})
			return
		}
		c.JSON(http.StatusOK, gin.H{
			"data": loans,
		})
	})

	r.GET("/api/loans/:id", func(c *gin.Context) {
		id := c.Param("id")
		loanId := int64(0)
		if _, err := fmt.Sscanf(id, "%d", &loanId); err != nil {
			c.JSON(http.StatusBadRequest, gin.H{
				"error": "Invalid loan id",
			})
			return
		}
		loan, err := fetchLoanById(apiUrl, loanId, token)
		if err != nil {
			c.JSON(http.StatusInternalServerError, gin.H{
				"error": err.Error(),
			})
			return
		}
		c.JSON(http.StatusOK, gin.H{
			"data": loan,
		})
	})

	r.POST("/api/loans", func(c *gin.Context) {
		var loan LoanDto
		if err := c.BindJSON(&loan); err != nil {
			c.JSON(http.StatusBadRequest, gin.H{
				"error": err.Error(),
			})
			return
		}
		if err := saveLoan(apiUrl, loan, token); err != nil {
			c.JSON(http.StatusInternalServerError, gin.H{
				"error": err.Error(),
			})
			return
		}
		c.JSON(http.StatusCreated, gin.H{
			"message": "Loan created successfully",
		})
	})

	if err := r.Run(":" + port); err != nil {
		log.Fatal().Msg("can' start server with 8080 port")
	}

}

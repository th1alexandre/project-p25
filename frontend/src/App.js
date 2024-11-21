import React, { useState, useEffect } from 'react';
import Modal from 'react-modal';
import 'react-responsive-modal/styles.css';
import './App.css'; 

Modal.setAppElement('#root');



function App() {
  const [listLoans, setListLoans] = useState([]);
  const [availableRates, setAvailableRates] = useState([]);
  const [newLoan, setNewLoan] = useState({
    name: '',
    description: '',
    value: 0,
    installmentsNumber: 0,
    startDate: '',
    rate: ''
  });
  const [modalIsOpen, setModalIsOpen] = useState(false);
  const [installmentsModalIsOpen, setInstallmentsModalIsOpen] = useState(false);
  const [selectedLoan, setSelectedLoan] = useState(null);
  const [installments, setInstallments] = useState([]);

  const loadRates = () => {
    fetch('http://localhost:8085/api/loans')
      .then((response) => response.json())
      .then((json) => {
        setListLoans(json.data);
      });
  };

  const loadAvailableRates = () => {
    fetch('http://localhost:8085/api/rates')
      .then((response) => response.json())
      .then((json) => {
        setAvailableRates(json.data);
      });
  };

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    if (name === 'rate') {
      setNewLoan({ ...newLoan, [name]: {
        id: parseInt(value),
      } });
    }
    else if (name === 'value') {
      setNewLoan({ ...newLoan, [name]: parseFloat(value) });
    } 
    else if (name === 'installmentsNumber') {
      setNewLoan({ ...newLoan, [name]: parseInt(value) });
    }
    else {
      setNewLoan({ ...newLoan, [name]: value });
    }
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    fetch('http://localhost:8085/api/loans', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(newLoan)
    })
    .then(response => response.json())
    .then(data => {
      loadRates();
      setNewLoan({
        name: '',
        description: '',
        value: 0,
        installmentsNumber: 0,
        startDate: '',
        rate: ''
      });
      setModalIsOpen(false);
    });
  };
  
  const handleRowClick = (loan) => {
    setSelectedLoan(loan);
    setInstallments(loan.installments);
    setInstallmentsModalIsOpen(true)
  };

  useEffect(() => {
    loadRates();
    loadAvailableRates();
  }, []);

  return (
    <div className="App">
      <button onClick={() => setModalIsOpen(true)}>Create New Loan</button>
      <Modal
        isOpen={modalIsOpen}
        onRequestClose={() => setModalIsOpen(false)}
        contentLabel="Create New Loan"
        className="modal"
        overlayClassName="overlay"
      >
        <form onSubmit={handleSubmit}>
          <h2>Create New Loan</h2>
          <label htmlFor="name">Name</label>
          <input type="text" id="name" name="name" placeholder="Name" value={newLoan.name} onChange={handleInputChange} required />
          
          <label htmlFor="description">Description</label>
          <input type="text" id="description" name="description" placeholder="Description" value={newLoan.description} onChange={handleInputChange} required />
          
          <label htmlFor="value">Value</label>
          <input type="number" id="value" name="value" placeholder="Value" value={newLoan.value} onChange={handleInputChange} required />
          
          <label htmlFor="installmentsNumber">Installments</label>
          <input type="number" id="installmentsNumber" name="installmentsNumber" placeholder="Installments" value={newLoan.installmentsNumber} onChange={handleInputChange} required />
          
          <label htmlFor="startDate">Start Date</label>
          <input type="date" id="startDate" name="startDate" placeholder="Start Date" value={newLoan.startDate} onChange={handleInputChange} required />
          
          <label htmlFor="rate">Rate</label>
          <select id="rate" name="rate" value={newLoan.rate.id} onChange={handleInputChange} required>
            <option value="">Select Rate</option>
            {availableRates.map((rate) => (
              <option key={rate.name} value={rate.id}>{`${rate.name}(${rate.value})`}</option>
            ))}
          </select>
          <button type="submit">Create Loan</button>
          <button type="button" onClick={() => setModalIsOpen(false)}>Cancel</button>
        </form>
      </Modal>
      <Modal
        isOpen={installmentsModalIsOpen}
        onRequestClose={() => setInstallmentsModalIsOpen(false)}
        contentLabel="Installments"
        className="modal"
        overlayClassName="overlay"
      >
      <h2>Installments for Loan {selectedLoan && selectedLoan.name}</h2>
      <table>
        <thead>
          <tr>
            <th>Installment Number</th>
            <th>Amount</th>
            <th>Due Date</th>
          </tr>
        </thead>
        <tbody>
          {installments.map((installment, index) => (
            <tr key={index}>
              <td>{installment.installmentNumber}</td>
              <td>{installment.value}</td>
              <td>{installment.dueDate}</td>
            </tr>
          ))}
        </tbody>
      </table>
        <button onClick={() => setInstallmentsModalIsOpen(false)}>Close</button>
      </Modal>
      <div className="table-container">
        <h2>Loan List</h2>
        <table>
          <thead>
            <tr>
              <th>ID</th>
              <th>Name</th>
              <th>Description</th>
              <th>Value</th>
              <th>Installments</th>
              <th>Start Date</th>
              <th>Rate</th>
            </tr>
          </thead>
          <tbody>
            {listLoans.map((rate) => (
              <tr key={rate.id} onClick={() => handleRowClick(rate)}>
                <td>{rate.id}</td>
                <td>{rate.name}</td>
                <td>{rate.description}</td>
                <td>{rate.value}</td>
                <td>{rate.installmentsNumber}</td>
                <td>{rate.startDate}</td>
                <td>{rate.rate.name}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}

export default App;
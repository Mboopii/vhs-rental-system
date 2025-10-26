import React, { useState } from 'react';
import axios from 'axios';
import styles from './AddUserForm.module.css';

function AddUserForm({ onUserAdded }) {
  const [name, setName] = useState('');
  const [email, setEmail] = useState('');
  const [error, setError] = useState(null);
  const [successMessage, setSuccessMessage] = useState('');
  const [isLoading, setIsLoading] = useState(false);

  const handleSubmit = async (event) => {
    event.preventDefault();
    setError(null);
    setSuccessMessage('');
    setIsLoading(true);

    //simple frontend validation
    if (!name.trim() || !email.trim()) {
      setError('Name and Email are required.');
      setIsLoading(false);
      return;
    }
    if (!/\S+@\S+\.\S+/.test(email.trim())) {
         setError('Please enter a valid email address.');
         setIsLoading(false);
         return;
    }

    const newUserData = { name: name.trim(), email: email.trim() };

    try {
      const response = await axios.post('http://localhost:8080/api/users', newUserData);
      setSuccessMessage(`User "${response.data.name}" added successfully (ID: ${response.data.id})!`);
      setName('');
      setEmail('');
      //call parent to refresh all lists
      if (onUserAdded) {
        onUserAdded();
      }
    } catch (err) {
      console.error("Error adding user:", err);
      //display backend validation/business logic errors
      if (err.response?.data) {
         if (err.response.data.errors) {
           const backendErrors = Object.values(err.response.data.errors).join('; ');
           setError(`Error: ${backendErrors}`);
         } else if (err.response.data.message) {
           setError(`Error: ${err.response.data.message}`);
         } else {
            setError(`Server error (Status: ${err.response.status}).`);
         }
      } else {
         setError('Could not send data. Check connection/backend.');
      }
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className={styles.formContainer}>
      <h3 className={styles.formTitle}>Add New User</h3>
      <form onSubmit={handleSubmit} className={styles.formElement}>
        <div className={styles.formRow}>
          <label htmlFor="user-name" className={styles.formLabel}>Name:</label>
          <input
            id="user-name" type="text" value={name}
            onChange={(e) => setName(e.target.value)}
            disabled={isLoading} required
            className={styles.formInput}
          />
        </div>
        <div className={styles.formRow}>
          <label htmlFor="user-email" className={styles.formLabel}>Email:</label>
          <input
            id="user-email" type="email" value={email}
            onChange={(e) => setEmail(e.target.value)}
            disabled={isLoading} required
            className={styles.formInput}
          />
        </div>
        <button
            type="submit"
            disabled={isLoading}
            className={`${styles.submitButton} ${isLoading ? styles.submitButtonLoading : styles.submitButtonEnabled}`}
        >
          {isLoading ? 'Adding...' : 'Add User'}
        </button>
      </form>
      {error && <p className={styles.errorMessage}>{error}</p>}
      {successMessage && <p className={styles.successMessage}>{successMessage}</p>}
    </div>
  );
}

export default AddUserForm;
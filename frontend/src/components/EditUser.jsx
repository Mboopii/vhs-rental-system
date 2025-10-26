import React, { useState, useEffect } from 'react';
import axios from 'axios';
import styles from './EditUser.module.css';

function EditUser({ user, onClose, onUserUpdated }) {
  //state for form fields, pre-filled with existing user data
  const [name, setName] = useState(user.name);
  const [email, setEmail] = useState(user.email);
  
  const [error, setError] = useState(null);
  const [isLoading, setIsLoading] = useState(false);

  useEffect(() => {
    setName(user.name);
    setEmail(user.email);
  }, [user]);

  const handleSubmit = async (event) => {
    event.preventDefault();
    setError(null);
    setIsLoading(true);

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

    const updatedUserData = { name: name.trim(), email: email.trim() };

    try {
      //send PUT request to the backend
      await axios.put(`http://localhost:8080/api/users/${user.id}`, updatedUserData);
      
      //success, tell parent to refresh and close modal
      if (onUserUpdated) {
        onUserUpdated();
      }
      onClose();

    } catch (err) {
      console.error("Error updating user:", err);
      //handle backend errors (like duplicate email)
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
    <div className={styles.modalOverlay} onClick={onClose}>
      <div className={styles.modalContent} onClick={(e) => e.stopPropagation()}>
        <h3 className={styles.modalTitle}>Edit User: {user.name}</h3>
        
        <form onSubmit={handleSubmit} className={styles.formElement}>
          <div className={styles.formRow}>
            <label htmlFor="edit-user-name" className={styles.formLabel}>Name:</label>
            <input
              id="edit-user-name" type="text" value={name}
              onChange={(e) => setName(e.target.value)}
              disabled={isLoading} required
              className={styles.formInput}
            />
          </div>
          <div className={styles.formRow}>
            <label htmlFor="edit-user-email" className={styles.formLabel}>Email:</label>
            <input
              id="edit-user-email" type="email" value={email}
              onChange={(e) => setEmail(e.target.value)}
              disabled={isLoading} required
              className={styles.formInput}
            />
          </div>
          
          {error && <p className={styles.errorMessage}>{error}</p>}

          <div className={styles.buttonContainer}>
            <button
                type="button"
                onClick={onClose}
                disabled={isLoading}
                className={styles.cancelButton}
            >
              Cancel
            </button>
            <button
                type="submit"
                disabled={isLoading}
                className={`${styles.submitButton} ${isLoading ? styles.submitButtonLoading : styles.submitButtonEnabled}`}
            >
              {isLoading ? 'Saving...' : 'Save Changes'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}

export default EditUser;
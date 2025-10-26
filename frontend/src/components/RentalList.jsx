import React, { useState } from 'react';
import axios from 'axios';
import styles from './RentalList.module.css';

function RentalList({ rentals, isLoading, currentUser, onActionComplete }) {
  
  //local state just for return button UI
  const [returnStatus, setReturnStatus] = useState({});

  const handleReturn = async (rentalId) => {
     if (!currentUser) return;
     setReturnStatus(prev => ({ ...prev, [rentalId]: { loading: true, error: null } }));
    try {
      await axios.post(`http://localhost:8080/api/rentals/return/${rentalId}`);
      setReturnStatus(prev => ({ ...prev, [rentalId]: { loading: false, error: null } }));
      if (onActionComplete) onActionComplete();
    } catch (err) {
      console.error("Error returning VHS:", err);
      let errorMessage = "Could not return VHS. Check backend.";
      if (err.response?.data?.message) errorMessage = err.response.data.message;
      setReturnStatus(prev => ({ ...prev, [rentalId]: { loading: false, error: errorMessage } }));
        setTimeout(() => {
            setReturnStatus(prev => ({ ...prev, [rentalId]: { ...prev[rentalId], error: null } }));
        }, 5000);
    }
  };

  if (isLoading) return <p className={styles.loadingText}>Loading rentals...</p>;

  return (
    <div>
      <h2 className={styles.title}>Rental List</h2>
      {rentals.length === 0 ? (
        <p className={styles.emptyListText}>No rentals found.</p>
      ) : (
        <div className={styles.tableContainer}>
          <table className={styles.table}>
            <thead className={styles.tableHeader}>
              <tr>
                <th scope="col" className={styles.th}>ID</th>
                <th scope="col" className={styles.th}>User</th>
                <th scope="col" className={styles.th}>VHS</th>
                <th scope="col" className={styles.th}>Rented</th>
                <th scope="col" className={styles.th}>Due</th>
                <th scope="col" className={styles.th}>Returned</th>
                <th scope="col" className={styles.th}>Late Fee</th>
                <th scope="col" className={`${styles.th} ${styles.thAction}`}>Action</th>
              </tr>
            </thead>
            <tbody className={styles.tableBody}>
              {rentals.map((rental) => {
                  const status = returnStatus[rental.id] || { loading: false, error: null };
                  //show return button only for the logged-in user's active rentals
                  const showReturnButton = currentUser === rental.user.id && rental.returnDate === null;
                  const isLate = rental.returnDate && rental.returnDate > rental.dueDate;

                  return (
                      <tr key={rental.id} className={styles.tableRow}>
                        <td className={`${styles.td} ${styles.tdId}`}>{rental.id}</td>
                        <td className={`${styles.td} ${styles.tdUser}`}>{rental.user.name}</td>
                        <td className={`${styles.td} ${styles.tdVhs}`}>{rental.vhs.title}</td>
                        <td className={`${styles.td} ${styles.tdRented}`}>{rental.rentalDate}</td>
                        <td className={`${styles.td} ${isLate ? styles.tdDueLate : styles.tdDue}`}>{rental.dueDate}</td>
                        <td className={`${styles.td} ${styles.tdReturned}`}>
                          {rental.returnDate ? rental.returnDate : <span className={styles.tdStatusText}>Not Returned</span>}
                        </td>
                        <td className={`${styles.td} ${styles.tdLateFee}`}>
                          {rental.lateFee != null ? `$${rental.lateFee.toFixed(2)}` : <span className={styles.tdStatusText}>N/A</span>}
                        </td>
                        <td className={`${styles.td} ${styles.tdAction}`}>
                          {showReturnButton && (
                            <div className={styles.actionWrapper}>
                              <button
                                onClick={() => handleReturn(rental.id)}
                                disabled={status.loading}
                                className={`${styles.returnButton} ${status.loading ? styles.returnButtonLoading : ''}`}
                              >
                                {status.loading ? 'Returning...' : 'Return'}
                              </button>
                              {status.error && <div className={styles.inlineError}>{status.error}</div>}
                            </div>
                          )}
                        </td>
                      </tr>
                  );
              })}
            </tbody>
          </table>
        </div>
      )}
    </div>
  );
}

export default RentalList;
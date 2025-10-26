import React, { useState } from 'react';
import axios from 'axios';
import styles from './UserList.module.css';

function UserList({ users, isLoading, onActionComplete, onEditUser }) {
  const [deleteStatus, setDeleteStatus] = useState({});

  const handleDelete = async (userId, userName) => {
    if (!window.confirm(`Are you sure you want to delete ${userName}? This cannot be undone.`)) {
      return;
    }

    setDeleteStatus(prev => ({ ...prev, [userId]: { loading: true, error: null } }));
    try {
      await axios.delete(`http://localhost:8080/api/users/${userId}`);
      setDeleteStatus(prev => ({ ...prev, [userId]: { loading: false, error: null } }));
      if (onActionComplete) onActionComplete();
    } catch (err) {
      console.error("Error deleting user:", err);
      let errorMessage = "Could not delete user.";
      if (err.response?.data?.message) errorMessage = err.response.data.message;
      setDeleteStatus(prev => ({ ...prev, [userId]: { loading: false, error: errorMessage } }));
      
      setTimeout(() => {
        setDeleteStatus(prev => ({ ...prev, [userId]: { ...prev[userId], error: null } }));
      }, 5000);
    }
  };

  if (isLoading) return <p className={styles.loadingText}>Loading users...</p>;

  return (
    <div>
      <h2 className={styles.title}>User List</h2>
      {users.length === 0 ? (
        <p className={styles.emptyListText}>No users found.</p>
      ) : (
        <ul className={styles.userList}>
          {users.map((user) => {
            const status = deleteStatus[user.id] || { loading: false, error: null };
            return (
              <li key={user.id} className={styles.userListItem}>
                <div className={styles.userInfo}>
                  <span className={styles.userId}>(ID: {user.id})</span>
                  <strong className={styles.userName}>{user.name}</strong> -
                  <span className={styles.userEmail}>{user.email}</span>
                </div>
                
                <div className={styles.actionWrapper}>
                  <button
                    onClick={() => onEditUser(user)}
                    disabled={status.loading}
                    className={styles.editButton}
                    title="Edit user"
                  >
                    ✎
                  </button>
                  <button
                    onClick={() => handleDelete(user.id, user.name)}
                    disabled={status.loading}
                    className={`${styles.deleteButton} ${status.loading ? styles.deleteButtonLoading : ''}`}
                    title="Delete user"
                  >
                    {status.loading ? '...' : '✖'}
                  </button>
                  {status.error && <div className={styles.inlineError}>{status.error}</div>}
                </div>
              </li>
            );
          })}
        </ul>
      )}
    </div>
  );
}

export default UserList;
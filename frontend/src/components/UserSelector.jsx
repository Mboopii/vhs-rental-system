import React, { useState } from 'react';
import styles from './UserSelector.module.css';

//component receives users as a prop, no local fetching
function UserSelector({ onUserSelect, users, isLoading }) {
  const [selectedUserId, setSelectedUserId] = useState('');

  const handleChange = (event) => {
    const userId = event.target.value;
    setSelectedUserId(userId);
    onUserSelect(userId ? parseInt(userId, 10) : null);
  };

  return (
    <div className={styles.selectorBar}>
      <label htmlFor="user-select" className={styles.label}>
        Select User (Simulated Login):
      </label>
      <select
        id="user-select"
        value={selectedUserId}
        onChange={handleChange}
        className={styles.dropdown}
        disabled={isLoading}
      >
        <option value="">-- {isLoading ? "Loading..." : "Not Logged In"} --</option>
        {users.map(user => (
          <option key={user.id} value={user.id}>
            {user.name} (ID: {user.id})
          </option>
        ))}
      </select>
    </div>
  );
}

export default UserSelector;
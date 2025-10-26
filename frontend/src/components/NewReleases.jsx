import React, { useState, useEffect, useCallback } from 'react';
import axios from 'axios';
import styles from './NewReleases.module.css';

function NewReleases({ refreshTrigger }) {
  const [vhsTapes, setVhsTapes] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const fetchNewReleases = useCallback(async () => {
    console.log("Fetching new releases...");
    setLoading(true);
    setError(null);
    try {
      const response = await axios.get('http://localhost:8080/api/vhs/new-releases');
      setVhsTapes(response.data);
    } catch (err) {
      console.error("Error fetching new releases:", err);
      setError("Could not fetch new releases.");
      setVhsTapes([]);
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    fetchNewReleases();
  }, [fetchNewReleases, refreshTrigger]);

  if (loading) return <p className={styles.loadingText}>Loading new releases...</p>;
  if (error) return <p className={styles.errorText}>Error: {error}</p>;

  return (
    <div className={styles.container}>
      <h3 className={styles.title}><strong>New Releases</strong></h3>
      {vhsTapes.length === 0 ? (
        <p className={styles.emptyListText}>No new releases found.</p>
      ) : (
        <ul className={styles.vhsList}>
          {vhsTapes.map((tape) => (
            <li key={tape.id} className={styles.vhsListItem}>
              <strong className={styles.vhsTitle}>{tape.title}</strong>
              <span className={styles.vhsDetails}>
                ({tape.releaseYear}) - {tape.genre}
              </span>
            </li>
          ))}
        </ul>
      )}
    </div>
  );
}

export default NewReleases;
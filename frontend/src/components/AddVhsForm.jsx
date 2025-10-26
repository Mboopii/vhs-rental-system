import React, { useState } from 'react';
import axios from 'axios';
import styles from './AddVhsForm.module.css';

function AddVhsForm({ onVhsAdded }) {
    const [title, setTitle] = useState('');
    const [genre, setGenre] = useState('');
    const [releaseYear, setReleaseYear] = useState('');
    const [error, setError] = useState(null);
    const [successMessage, setSuccessMessage] = useState('');
    const [isLoading, setIsLoading] = useState(false);
    const availableGenres = ['Action', 'Animation', 'Comedy', 'Crime', 'Drama', 'Horror', 'Romance', 'Sci-Fi', 'Thriller'];
    const handleSubmit = async (event) => {
        event.preventDefault();
        setError(null);
        setSuccessMessage('');
        setIsLoading(true);

        //frontend validation
        if (!title.trim() || !genre || !releaseYear) {
            setError('All fields are required.');
            setIsLoading(false);
            return;
        }
        const yearNumber = parseInt(releaseYear);
        if (isNaN(yearNumber) || yearNumber < 1900) {
            setError('Release year must be a number >= 1900.');
            setIsLoading(false);
            return;
        }

        const newVhsData = {
            title: title.trim(),
            genre: genre,
            releaseYear: yearNumber
        };

        try {
            const response = await axios.post('http://localhost:8080/api/vhs', newVhsData);
            setSuccessMessage(`VHS "${response.data.title}" added successfully (ID: ${response.data.id})!`);
            setTitle('');
            setGenre('');
            setReleaseYear('');
            //call parent to refresh lists
            if (onVhsAdded) {
                onVhsAdded();
            }
        } catch (err) {
            console.error("Error adding VHS:", err);
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
            <h3 className={styles.formTitle}>Add New VHS Tape</h3>
            <form onSubmit={handleSubmit} className={styles.formElement}>
                <div className={styles.formRow}>
                    <label htmlFor="vhs-title" className={styles.formLabel}>Title:</label>
                    <div className={styles.inputWrapper}>
                        <input
                            id="vhs-title" type="text" value={title}
                            onChange={(e) => setTitle(e.target.value)}
                            disabled={isLoading} required
                            className={styles.formInput}
                        />
                    </div>
                </div>

                <div className={styles.formRow}>
                    <label htmlFor="vhs-genre" className={styles.formLabel}>Genre:</label>
                    <div className={styles.inputWrapper}>
                        <select
                            id="vhs-genre"
                            value={genre}
                            onChange={(e) => setGenre(e.target.value)}
                            disabled={isLoading}
                            required
                            className={styles.formSelect}
                        >
                            <option value="" disabled>-- Select Genre --</option>
                            {availableGenres.map(g => (
                                <option key={g} value={g}>{g}</option>
                            ))}
                        </select>
                    </div>
                </div>
                
                <div className={styles.formRow}>
                    <label htmlFor="vhs-year" className={styles.formLabel}>Year:</label>
                    <div className={styles.inputWrapper}>
                        <input
                            id="vhs-year" type="number" value={releaseYear}
                            onChange={(e) => setReleaseYear(e.target.value)}
                            disabled={isLoading} required min="1900"
                            className={styles.formInput}
                        />
                    </div>
                </div>

                <div className={styles.buttonWrapper}>
                    <button
                        type="submit"
                        disabled={isLoading}
                        className={`${styles.submitButton} ${isLoading ? styles.submitButtonLoading : styles.submitButtonEnabled}`}
                    >
                        {isLoading ? 'Adding...' : 'Add VHS'}
                    </button>
                </div>
            </form>
            {error && <p className={styles.errorMessage}>{error}</p>}
            {successMessage && <p className={styles.successMessage}>{successMessage}</p>}
        </div>
    );
}

export default AddVhsForm;
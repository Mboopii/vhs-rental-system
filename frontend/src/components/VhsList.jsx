import React, { useState, useEffect, useCallback } from 'react';
import axios from 'axios';
import styles from './VhsList.module.css';

function VhsList({ refreshTrigger, currentUser, rentedVhsIds, onActionComplete }) {
    const [vhsTapes, setVhsTapes] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [rentStatus, setRentStatus] = useState({});
    const [deleteStatus, setDeleteStatus] = useState({});

    const [filterGenre, setFilterGenre] = useState('');
    const [filterYear, setFilterYear] = useState('');
    const availableGenres = ['Action', 'Animation', 'Comedy', 'Crime', 'Drama', 'Horror', 'Romance', 'Sci-Fi', 'Thriller'];
    const [showYearPicker, setShowYearPicker] = useState(false);
    const [decadeStartYear, setDecadeStartYear] = useState(Math.floor(new Date().getFullYear() / 10) * 10);
   
    const fetchVhsTapes = useCallback(async () => {
        console.log(`Fetching VHS tapes with filters: Genre='${filterGenre}', Year='${filterYear}'`);
        setLoading(true);
        setError(null);
        const params = {};
        if (filterGenre) params.genre = filterGenre;
        if (filterYear && !isNaN(parseInt(filterYear))) params.year = parseInt(filterYear);
        try {
            const response = await axios.get('http://localhost:8080/api/vhs', { params });
            setVhsTapes(response.data);
        } catch (err) {
            console.error("Error fetching VHS tapes:", err);
            setError("Could not fetch VHS tapes. Check backend/CORS.");
            setVhsTapes([]);
        } finally {
            setLoading(false);
        }
    }, [filterGenre, filterYear]);

    useEffect(() => {
        fetchVhsTapes();
    }, [fetchVhsTapes, refreshTrigger]);

    const handleRent = async (vhsId) => {
        if (!currentUser) {
            alert("Please select a user first from the dropdown at the top!");
            return;
        }
        setRentStatus(prev => ({ ...prev, [vhsId]: { loading: true, error: null } }));
        try {
            await axios.post('http://localhost:8080/api/rentals', { userId: currentUser, vhsId: vhsId });
            setRentStatus(prev => ({ ...prev, [vhsId]: { loading: false, error: null } }));
            if (onActionComplete) onActionComplete();
        } catch (err) {
            console.error("Error renting VHS:", err);
            let errorMessage = "Could not rent VHS. Check backend.";
            if (err.response?.data?.message) errorMessage = err.response.data.message;
            setRentStatus(prev => ({ ...prev, [vhsId]: { loading: false, error: errorMessage } }));
            setTimeout(() => {
               setRentStatus(prev => ({ ...prev, [vhsId]: { ...prev[vhsId], error: null } }));
            }, 5000);
        }
    };

    //confirm before deleting vhs
    const handleDelete = async (vhsId, vhsTitle) => {
      if (!window.confirm(`Are you sure you want to delete "${vhsTitle}"? This is permanent.`)) {
        return;
      }

      setDeleteStatus(prev => ({ ...prev, [vhsId]: { loading: true, error: null } }));
      try {
        await axios.delete(`http://localhost:8080/api/vhs/${vhsId}`);
        setDeleteStatus(prev => ({ ...prev, [vhsId]: { loading: false, error: null } }));
        if (onActionComplete) onActionComplete();
      } catch (err) {
        console.error("Error deleting VHS:", err);
        let errorMessage = "Could not delete VHS.";
        if (err.response?.data?.message) errorMessage = err.response.data.message;
        setDeleteStatus(prev => ({ ...prev, [vhsId]: { loading: false, error: errorMessage } }));
        setTimeout(() => {
          setDeleteStatus(prev => ({ ...prev, [vhsId]: { ...prev[vhsId], error: null } }));
        }, 5000);
      }
    };
    
    const handleYearSelect = (year) => { setFilterYear(year.toString()); setShowYearPicker(false); };
    const clearYearFilter = () => { setFilterYear(''); setShowYearPicker(false); };
    const changeDecade = (amount) => { setDecadeStartYear(prev => prev + amount * 10); };
    const getYearsForGrid = (startYear) => { const years = []; for (let i = 0; i < 12; i++) years.push(startYear + i); return years; };

    if (loading) return <p className={styles.loadingText}>Loading VHS list...</p>;
    if (error) return <p className={styles.errorText}>Error: {error}</p>;

    return (
        <div>
            <div className={styles.filterContainer}>
                 <h2 className={styles.title}>VHS Collection</h2>
                 <div className={styles.controlsContainer}>
                     <div>
                         <label htmlFor="genre-filter" className={styles.filterLabel}>Genre:</label>
                         <select id="genre-filter" value={filterGenre} onChange={(e) => setFilterGenre(e.target.value)} className={styles.genreSelect} >
                             <option value="">All Genres</option>
                             {availableGenres.map(genre => (<option key={genre} value={genre}>{genre}</option>))}
                         </select>
                     </div>
                     <div className="flex items-center">
                         <label className={styles.filterLabel}>Year:</label>
                         <button type="button" onClick={() => setShowYearPicker(!showYearPicker)} className={styles.yearButton} >
                             {filterYear || 'Select Year'}
                             <svg className={styles.yearButtonArrow} xmlns="http://www.w3.org/2000/svg" viewBox="0 0 20 20" fill="currentColor" aria-hidden="true">
                                 <path fillRule="evenodd" d="M5.293 7.293a1 1 0 011.414 0L10 10.586l3.293-3.293a1 1 0 111.414 1.414l-4 4a1 1 0 01-1.414 0l-4-4a1 1 0 010-1.414z" clipRule="evenodd" />
                             </svg>
                         </button>
                         {filterYear && (<button type="button" onClick={clearYearFilter} className={styles.clearYearButton} title="Clear year filter"> &#x2715; </button>)}
                     </div>
                    {showYearPicker && (
                        <div className={styles.yearPicker}>
                            <div className={styles.decadeNav}>
                                <button onClick={() => changeDecade(-1)} className={styles.decadeButton}>&lt;</button>
                                <span className={styles.decadeLabel}>{decadeStartYear} - {decadeStartYear + 11}</span>
                                <button onClick={() => changeDecade(1)} className={styles.decadeButton}>&gt;</button>
                            </div>
                            <div className={styles.yearGrid}>
                                {getYearsForGrid(decadeStartYear).map(year => (
                                    <button key={year} type="button" onClick={() => handleYearSelect(year)}
                                        className={`${styles.yearGridButton} ${ parseInt(filterYear) === year ? styles.yearGridButtonSelected : styles.yearGridButtonDefault }`}
                                    > {year} </button>
                                ))}
                            </div>
                             <button type="button" onClick={clearYearFilter} className={styles.clearYearPickerButton}> Clear Year </button>
                        </div>
                    )}
                 </div>
            </div>


            {vhsTapes.length === 0 && !loading ? (
                <p className={styles.emptyListText}>No VHS tapes found matching the criteria.</p>
            ) : (
                <ul className={styles.vhsListUl}>
                    {vhsTapes.map((tape) => {
                        const rentState = rentStatus[tape.id] || { loading: false, error: null };
                        const deleteState = deleteStatus[tape.id] || { loading: false, error: null };
                        //check if vhs is already rented
                        const isRented = rentedVhsIds.has(tape.id);
                        const isSomeoneLoading = rentState.loading || deleteState.loading;

                        return (
                            <li key={tape.id} className={`${styles.vhsListItem} group`}>
                                <div>
                                    <strong className={styles.vhsDetailsTitle}>{tape.title}</strong>
                                    <span className={styles.vhsDetailsYear}>({tape.releaseYear})</span>
                                    <p className={styles.vhsDetailsGenre}>{tape.genre}</p>
                                </div>
                                
                                <div className={styles.actionContainer}>
                                    <button
                                        onClick={() => handleRent(tape.id)}
                                        disabled={!currentUser || isSomeoneLoading || isRented}
                                        className={`${styles.rentButton} ${
                                            isRented ? styles.rentButtonRented :
                                            !currentUser ? styles.rentButtonNoUser :
                                            rentState.loading ? styles.rentButtonLoading : styles.rentButtonAvailable
                                        }`}
                                    >
                                        {isRented ? 'Rented' : rentState.loading ? '...' : 'Rent'}
                                    </button>
                                    
                                    <button
                                        onClick={() => handleDelete(tape.id, tape.title)}
                                        //disable delete if rented
                                        disabled={isSomeoneLoading || (isRented && !currentUser)}
                                        className={`${styles.deleteButton} ${deleteState.loading ? styles.deleteButtonLoading : ''} ${isRented ? 'disabled:opacity-25' : ''}`}
                                        title="Delete VHS"
                                    >
                                        {deleteState.loading ? '...' : '✖'}
                                    </button>

                                    {rentState.error && <p className={styles.rentError}>{rentState.error}</p>}
                                    {deleteState.error && <p className={styles.rentError}>{deleteState.error}</p>}
                                </div>
                            </li>
                        );
                    })}
                </ul>
            )}
        </div>
    );
}

export default VhsList;
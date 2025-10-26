import React, { useState, useEffect, useMemo, useCallback } from 'react';
import axios from 'axios';
import VhsList from './components/VhsList';
import AddVhsForm from './components/AddVhsForm';
import UserList from './components/UserList';
import AddUserForm from './components/AddUserForm';
import RentalList from './components/RentalList';
import UserSelector from './components/UserSelector';
import NewReleases from './components/NewReleases';
import EditUser from './components/EditUser';
import styles from './App.module.css';

function App() {
  //main app state
  const [vhsRefreshKey, setVhsRefreshKey] = useState(0);
  const [userRefreshKey, setUserRefreshKey] = useState(0);
  const [rentalRefreshKey, setRentalRefreshKey] = useState(0);
  //... (svi postojeći state-ovi)
  const [currentUser, setCurrentUser] = useState(null);
  
  // --- 2. DODAJTE NOVI STATE ZA MODAL ---
  const [editingUser, setEditingUser] = useState(null); //null = modal zatvoren

  //... (svi postojeći state-ovi za users i rentals)
  const [allUsers, setAllUsers] = useState([]);
  const [userError, setUserError] = useState(null);
  const [userLoading, setUserLoading] = useState(true);
  const [allRentals, setAllRentals] = useState([]);
  const [rentalError, setRentalError] = useState(null);
  const [rentalLoading, setRentalLoading] = useState(true);
  
  //... (useEffect hookovi za dohvaćanje ostaju isti)
  useEffect(() => {
    const fetchUsers = async () => {
      console.log("App fetching users...");
      setUserLoading(true);
      setUserError(null);
      try {
        const response = await axios.get('http://localhost:8080/api/users');
        setAllUsers(response.data);
      } catch (err) {
        console.error("App: Error fetching users:", err);
        setUserError("Could not load user data.");
        setAllUsers([]);
      } finally {
        setUserLoading(false);
      }
    };
    fetchUsers();
  }, [userRefreshKey]);
  useEffect(() => {
    const fetchRentals = async () => {
      console.log("App fetching rentals...");
      setRentalLoading(true);
      setRentalError(null);
      let url = 'http://localhost:8080/api/rentals';
      if (currentUser) {
        url = `http://localhost:8080/api/users/${currentUser}/rentals`;
      }
      try {
        const response = await axios.get(url);
        setAllRentals(response.data);
      } catch (err) {
        console.error("App: Error fetching rentals:", err);
        setRentalError("Could not load rental data.");
        setAllRentals([]); 
      } finally {
        setRentalLoading(false);
      }
    };
    fetchRentals();
  }, [rentalRefreshKey, currentUser]);

  const rentedVhsIds = useMemo(() => {
    return new Set(
      allRentals
        .filter(rental => rental.returnDate === null)
        .map(rental => rental.vhs.id)
    );
  }, [allRentals]);

  const refreshAllLists = useCallback(() => {
    console.log("Refreshing all lists...");
    setVhsRefreshKey(k => k + 1);
    setUserRefreshKey(k => k + 1);
    setRentalRefreshKey(k => k + 1);
    // --- 3. AŽURIRAJTE REFRESH DA ZATVORI MODAL ---
    setEditingUser(null); //zatvori modal nakon uspješnog updatea
  }, []);

  const handleUserSelect = (userId) => {
    setCurrentUser(userId);
    console.log("Current user set to ID:", userId);
  };
  
  // --- 4. FUNKCIJA ZA OTVARANJE MODALA ---
  const handleEditUser = (user) => {
    console.log("Editing user:", user);
    setEditingUser(user);
  };

  return (
    <div className={styles.pageContainer}>
      <UserSelector 
        onUserSelect={handleUserSelect} 
        users={allUsers}
        isLoading={userLoading} 
      />

      <main className={styles.mainContent}>
        {/* ... (h1 i status messages ostaju isti) ... */}
        <h1 className={styles.mainTitle}>VHS Rental Store</h1>
        <div className={styles.statusMessageContainer}>
          {currentUser ? (
            <p className={styles.statusWelcome}>
              Welcome! Managing rentals for user ID: <strong className={styles.statusUserId}>{currentUser}</strong>
            </p>
          ) : (
            <p className={styles.statusWarning}>
              ⚠️ Please select a user from the dropdown above to manage rentals.
            </p>
          )}
          {rentalError && <p className={styles.statusError}>{rentalError}</p>}
          {userError && <p className={styles.statusError}>{userError}</p>}
        </div>

        <div className={styles.layoutGrid}>
          {/* ... (RentalList sekcija ostaje ista) ... */}
          <section className={styles.sectionCardFull}>
            <RentalList
                rentals={allRentals}
                isLoading={rentalLoading}
                currentUser={currentUser}
                onActionComplete={refreshAllLists}
            />
          </section>

          {/* ... (VhsList sekcija ostaje ista) ... */}
          <section className={styles.sectionCard}>
            <VhsList
                refreshTrigger={vhsRefreshKey}
                currentUser={currentUser}
                rentedVhsIds={rentedVhsIds}
                onActionComplete={refreshAllLists}
            />
            <hr className={styles.divider} />
            <AddVhsForm onVhsAdded={refreshAllLists} />
          </section>

          <section className={styles.sectionCard}>
            {/* --- 5. PROSLIJEDITE NOVU FUNKCIJU U UserList --- */}
            <UserList 
              users={allUsers} 
              isLoading={userLoading} 
              onActionComplete={refreshAllLists}
              onEditUser={handleEditUser} // <-- Dodajte ovaj prop
            />
            <hr className={styles.divider} />
            <AddUserForm onUserAdded={refreshAllLists} />
            <NewReleases refreshTrigger={vhsRefreshKey} />
          </section>

        </div>
      </main>
      
      {editingUser && (
        <EditUser
          user={editingUser}
          onClose={() => setEditingUser(null)}
          onUserUpdated={refreshAllLists}
        />
      )}
    </div>
  );
}

export default App;
const mutations = {
    updateUserAuth(state, newUser) {
        localStorage.setItem('userAuth', JSON.stringify(newUser));
        state.user = newUser;
    }
};

export default mutations;

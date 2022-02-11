const state = {
    user: JSON.parse(localStorage.getItem("userAuth")) || {}
};

export default state;

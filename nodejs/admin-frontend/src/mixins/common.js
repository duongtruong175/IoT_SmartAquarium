import axios from 'axios'

axios.defaults.baseURL = 'http://localhost:1880/';

const common = {
    methods: {
        async callApi(method, url, dataObj) {
            try {
                const res = await axios({
                    method: method,
                    url: url,
                    data: dataObj
                });
                return res;
            } catch (error) {
                return error.response;
            }
        },
        formatDateTime(dateTime) {
            const d = new Date(dateTime);
            let day = d.getDate();
            let month = d.getMonth() + 1;
            const year = d.getFullYear();
            if (day < 10) {
                day = "0" + day;
            }
            if (month < 10) {
                month = "0" + month;
            }
            return day + "/" + month + "/" + year;
        }
    }
};

export default common;
import axios from 'axios';

const BASE_SERVICE_URL = process.env.REACT_APP_SERVICE_URL;
const API_SERVICE_URL = BASE_SERVICE_URL + "/api/services";

class MonitorService {
    findAll() {
        return axios.get(`${API_SERVICE_URL}`);
    }

    delete(serviceId) {
        return axios.delete(`${API_SERVICE_URL}/${serviceId}`);
    }

    save(service) {
        return axios.post(`${API_SERVICE_URL}`, service, {
            headers: { 'Content-Type': 'application/json' },
          });
    }
}

export default new MonitorService();
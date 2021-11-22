import { Component } from 'react';
import { Table, Button, Form } from 'react-bootstrap';
import MonitorService from '../services/MonitorService'

class ServiceTable extends Component {
    constructor(props) {
        super(props);
        this.state = {
            services: [],
            serviceUrl: "",
            serviceName: "",
            serviceStatus: "",
            isInvalidUrl: false,
            isInvalidData: false
        };

        this.refreshServicesTable = this.refreshServicesTable.bind(this);
        this.delete = this.delete.bind(this);
        this.saveService = this.saveService.bind(this);
        this.onServiceNameChange = this.onServiceNameChange.bind(this);
        this.onServiceUrlChange = this.onServiceUrlChange.bind(this);
    }

    componentDidMount() {
        this.refreshServicesTable();
        this.interval = setInterval(() => 
            this.refreshServicesTable(), 10000
        );
    }

    refreshServicesTable() {
        MonitorService.findAll()
            .then(response => {
                this.setState({
                    services: response.data
                });
            })
            .catch(error => {
                console.log(`Error while fetching all services ${error.message}`);
            });
    }

    delete(serviceId) {
        console.log("Service ID: " + serviceId);
        MonitorService.delete(serviceId)
            .then(response => {
                console.log(`Service with id ${serviceId} has been deleted`);
                this.refreshServicesTable();
            });
    }

    saveService() {
        if (!this.state.serviceName || !this.state.serviceUrl) {
            this.setState({
                ...this.state,
                isInvalidData: true
            });
            return;
        } else if (this.state.isInvalidUrl) {
            return;
        }

        var data = {
            name: this.state.serviceName,
            url: this.state.serviceUrl
        }

        MonitorService.save(data)
            .then(response => {
                console.log("Service saved!", response)
                this.refreshServicesTable();
            }).catch(error => {
                console.log("Error while saving a service: ", error)
            });
    }

    onServiceUrlChange(input) {
        try {
            new URL(input.target.value);
            console.log("Valid url")
            this.setState({
                isInvalidData: false,
                isInvalidUrl: false
            })
        } catch (_) {
            this.setState({
                isInvalidUrl: true
            })
        }

        this.setState({
            serviceUrl: input.target.value,
        })
    }

    onServiceNameChange(input) {
        this.setState({
            ...this.state,
            isInvalidData: false,
            serviceName: input.target.value
        })
    }

    showError() {
        if (this.state.serviceUrl && this.state.isInvalidUrl) {
            return (
                <div className="alert alert-danger error-box" role="alert">
                    Invalid URL
                </div>
            )
        } else if (this.state.isInvalidData) {
            return (
                <div className="alert alert-danger error-box" role="alert">
                    Both service name and url are required
                </div>
            )
        }
    }

    render() {
        return (
            <div>
                <div className="row mb-5">
                    <div className="col-7">
                        <Form.Control type="text"
                            id="serviceUrl"
                            value={this.state.serviceUrl}
                            onChange={this.onServiceUrlChange}
                            placeholder="URL: https://mock.codes/200" />
                        <span>
                            {this.showError()}
                        </span>
                    </div>
                    <div className="col-3">
                        <Form.Control type="text"
                            id="serviceName"
                            value={this.state.serviceName}
                            onChange={this.onServiceNameChange}
                            placeholder="Service name" />
                    </div>
                    <div className="col-2">
                        <Button className="w-100"
                            disabled={this.state.isInvalidData ||
                                this.state.isInvalidUrl ||
                                !this.state.serviceName ||
                                !this.state.serviceUrl}
                            onClick={this.saveService}>
                            Save</Button>
                    </div>
                </div>
                <Table striped bordered hover>
                    <thead>
                        <tr>
                            <th>#</th>
                            <th>Name</th>
                            <th>URL</th>
                            <th>Created</th>
                            <th>Status</th>
                            <th>Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        {this.state.services.map((service, i) => {
                            return (
                                <tr key={service.id}>
                                    <td>{service.id}</td>
                                    <td>{service.name}</td>
                                    <td>{service.url}</td>
                                    <td>{service.created}</td>
                                    <td>{service.status === 'OK' ?
                                        <span className="bg-success p-2 text-white d-block">OK</span> :
                                        <span className="bg-danger p-2 text-white d-block">FAIL</span>
                                    }</td>
                                    <td>
                                        <Button variant="danger"
                                            onClick={() => this.delete(service.id)}>X</Button>
                                    </td>
                                </tr>
                            )
                        })}
                    </tbody>
                </Table>
            </div>
        )
    }
}

export default ServiceTable;
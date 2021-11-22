import './css/App.css';
import './css/bootstrap.min.css';
import ServiceTable from './components/ServiceTable'

function App() {
  return (
    <div className="App">
      <header className="App-header">
        <h2 className="p-3">
          Service monitor
        </h2>
        <div className="container justify-content-center">
          <div className="col-12">
            <ServiceTable />
          </div>
        </div>
      </header>
    </div>
  );
}

export default App;

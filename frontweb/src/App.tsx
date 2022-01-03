// import React from 'react'; // This import is not necessary withing modules after React v17
import './assets/styles/custom.scss';
import Navbar from './components/Navbar';
import './App.css';

// we can also use lambda notation:
// const App =() => {
function App() {
   return (
      <>
         <Navbar />
         <h1>Hello DSCatalog</h1>
      </>
   ); // A JSX connot return more than one element, do we must wrap it in a div or just in a fragment, as we did here
}

export default App;

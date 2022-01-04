// for each component the folder will be named as the same as the component, and the tsx file will be index

import './styles.css';
import 'bootstrap/js/src/collapse.js';

import './styles.css';

const Navbar = () => {
   return (
      <nav className="navbar navbar-expand-md navbar-dark bg-primary main-nav">
         <div className="container-fluid" >
            <a href="link" className="nav-logo-text">
               <h4>DS Catalog</h4>
            </a>
            <button
               className="navbar-toggler"
               type="button"
               data-bs-toggle="collapse"
               data-bs-target="#dscatalog-navbar"
               aria-controls="dscatalog-navbar"
               aria-expanded="false"
               aria-label="Toggle navigation"
            >
               <span className="navbar-toggler-icon"></span>
            </button>

            <div className="collapse navbar-collapse" id="dscatalog-navbar">
               <ul className="navbar-nav offset-md-2 main-menu">
                  <li>
                     <a href="link" className="active">HOME</a>
                  </li>
                  <li>
                     <a href="link">CATÁLOGO</a>
                  </li>
                  <li>
                     <a href="link">ADMIN</a>
                  </li>
               </ul>
            </div>
         </div>
      </nav>
   ); // A JSX connot return more than one element, do we must wrap it in a div or just in a fragment, as we did here
};

export default Navbar;

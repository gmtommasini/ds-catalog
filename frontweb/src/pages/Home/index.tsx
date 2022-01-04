import Navbar from "components/Navbar";
import { ReactComponent as MainImage } from 'assets/images/MainImage.svg'

import './styles.css';

const Home = () => {
    return (
        <>
            <Navbar />
            <div className="home-container">
                <div className="home-card">
                    <div className="home-content-container">
                        <h1>
                            Conheça o melhor catalogo de produtos
                        </h1>
                    </div>
                    <div className="home-image-container">
                        <MainImage></MainImage>
                    </div>
                </div>
            </div>
        </>
    ); // A JSX connot return more than one element, do we must wrap it in a div or just in a fragment, as we did here
}

export default Home;

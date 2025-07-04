import {createContext, useState} from "react";
import {AppConstants} from "../util/constants.js";

export const AppContext = createContext();

export const AppContextProvider = (props) => {

    const backendURL = AppConstants.BACKEND_URL;
    const [isLoggedIn, setIsLoggedIn] =  useState(false);
    const [userData, setUserData] = useState(false);

    const contextValue = {
             backendURL,
             isLoggedIn, setIsLoggedIn,
             userData, setUserData,
    }

    return (
        <AppContextProvider value={contextValue}>
            {props.children}
        </AppContextProvider>
    )
}
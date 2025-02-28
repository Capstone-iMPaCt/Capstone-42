import authReducer from './authReducer'
import courseReducer from './courseReducer'
import { combineReducers } from 'redux'
import { firebaseReducer } from 'react-redux-firebase'
import { firestoreReducer } from 'redux-firestore'

const rootReducer = combineReducers({
    auth: authReducer,
    course: courseReducer,
    firebase: firebaseReducer,
    firestore: firestoreReducer 
})

export default rootReducer
import nfp from "../fixtures/nfp";

const initialState = {
  headerDisplay : false,
  existingPokemons: [],
  nfps: {
    "1": [nfp, nfp],
    "2": [nfp],
  },
  existingNfps: [],
  jwt: '',
  balance: '',
  isMusicMuted: true,
  gachaOrder: 0,
  supportOrder: 0,
  cardgameOrder: 0,
  cardgameFlipped: false,

};

export default function reducer(state = initialState, action) {
  if (action.type === 'updateHeaderDisplay') {
    return {
      ...state,
      // TODO : 바뀐값 넣기
      headerDisplay: action.payload.headerDisplay
    };
  }
  if (action.type === 'updateJwt'){
    return {
      ...state,
      jwt: action.payload.jwt
    };
  }
  if (action.type === 'updateBalance') {
    return {
      ...state,
      balance: action.payload.balance
    };
  }
  if (action.type === 'setExistingPokemons') {
    return {
      ...state,
      existingPokemons: action.payload.existingPokemons
    };
  }
  if (action.type === 'setExistingNfps') {
    return {
      ...state,
      existingNfps: action.payload.existingNfps
    };
  }
  if (action.type === 'setIsMusicMuted') {
    return {
      ...state,
      isMusicMuted: action.payload.isMusicMuted
    };
  }

  if (action.type === 'setGachaOrder') {
    return {
      ...state,
      gachaOrder: action.payload.gachaOrder
    };
  }

  if (action.type === 'setSupportOrder') {
    return {
      ...state,
      supportOrder: action.payload.supportOrder
    };
  }

  if (action.type === 'setCardgameOrder') {
    return {
      ...state,
      cardgameOrder : action.payload.cardgameOrder
    };
  }

  if (action.type === 'setCardgameFlipped') {
    return {
      ...state,
      cardgameFlipped: action.payload.cardgameFlipped
    };
  }
  return state;
}

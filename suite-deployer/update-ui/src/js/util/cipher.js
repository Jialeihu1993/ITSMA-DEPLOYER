import CryptoJS from 'crypto-js';


/**
 * sample :: cipher('AES','my message', 'secret key 123');
 *  */
export function cipher(algorithm, buf, token){
    var encrypted = '';
    encrypted = CryptoJS[algorithm].encrypt(buf, token);
    return encrypted.toString();
}
/**
 * sample :: decipher('AES','U2FsdGVkX19F1BYOTKnrEEaEIP0jH1kDaN8/6V7CKYU=', 'secret key 123');
 */
export function decipher(algorithm, buf, token){
    var decrypted = '';
    decrypted = CryptoJS[algorithm].decrypt(buf, token);
    return decrypted.toString(CryptoJS.enc.Utf8);
}

export function cipherPassword(algorithm, obj, token) {
    var newObj = deepCopy(obj);
    for(let i in obj) {
        if(i == 'password') {
            newObj[i]['currentVal'] = cipher(algorithm, obj[i]['currentVal'], token);
            break;
        }
        if( i == 'sm_integration_password'|| i == 'sm_database_password') {
            newObj[i]['currentVal'] = cipher(algorithm, obj[i]['currentVal'], token);
        }
    }
    return newObj;
}

export function deepCopy(source) {
    var result={};
    for (var key in source) {
        result[key] = typeof source[key]==='object'? deepCopy(source[key]): source[key];
    }
   return result;
}
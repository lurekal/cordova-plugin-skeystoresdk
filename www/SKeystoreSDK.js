var exec = require('cordova/exec');


var SKeystoreSDK = {
    getInstance: function (success, error) {
        exec(success, error, 'SKeystoreSDK', 'getInstance', []);
    },
    getKeystoreApiLevel: function (success, error) {
        exec(success, error, 'SKeystoreSDK', 'getKeystoreApiLevel', []);
    },
    checkMandatoryAppUpdate: function (success, error) {
        exec(success, error, 'SKeystoreSDK', 'checkMandatoryAppUpdate', []);
    },
    getSeedHash: function (success, error) {
        exec(success, error, 'SKeystoreSDK', 'getSeedHash', []);
    },
    getAddress: function (index, success, error) {
        exec(success, error, 'SKeystoreSDK', 'getAddress', [index]);
    },
    getPublicKey: function (index, success, error) {
        exec(success, error, 'SKeystoreSDK', 'getPublicKey', [index]);
    },
    signTransaction: function (rawTransaction, addressIndex, success, error) {
        exec(success, error, 'SKeystoreSDK', 'signTransaction', [rawTransaction, addressIndex]);
    },
    signPersonalMessage: function (message, addressIndex, success, error) {
        exec(success, error, 'SKeystoreSDK', 'signPersonalMessage', [message, addressIndex]);
    },
    linkSDK: function (urlcode, success, error) {
        exec(success, error, 'SKeystoreSDK', 'linkSDK', [urlcode]);
    }



}



module.exports = SKeystoreSDK;

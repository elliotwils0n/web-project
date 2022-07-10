
export class Credentials {

    username: string;
    password: string;
    repeatPassword: string;

    constructor(username: string, password: string, repeatPassword: string) {
        this.username = username;
        this.password = password;
        this.repeatPassword = repeatPassword;
    }
    
}
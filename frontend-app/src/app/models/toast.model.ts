
export class Toast {
    
    title: string;
    message: string;
    date: Date;

    constructor(title: string, message: string, date: Date) {
        this.title = title;
        this.message = message;
        this.date = date;
    }
    
}
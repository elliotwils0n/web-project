import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';

@Component({
  selector: 'app-toast',
  templateUrl: './toast.component.html',
  styleUrls: ['./toast.component.css']
})
export class ToastComponent implements OnInit {
  
  @Output() closeHit: EventEmitter<boolean> = new EventEmitter<boolean>();
  @Input() title: string = '';
  @Input() message: string = '';
  @Input() date: Date = new Date();

  constructor() { }

  ngOnInit(): void {
    var toastElements = [].slice.call(document.querySelectorAll('.toast'));
    toastElements.map(function (toastEl) {
        return new bootstrap.Toast(toastEl, {});
    })
  }

}

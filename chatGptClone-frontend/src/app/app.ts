import { Component, OnInit, inject } from '@angular/core';
import {CommonModule, NgClass} from '@angular/common';
import { FormBuilder, FormGroup, FormsModule } from '@angular/forms';
import { Ai } from './service/ai.service';
import { catchError, throwError } from 'rxjs';
import { ReactiveFormsModule } from '@angular/forms';
import { ChangeDetectorRef } from '@angular/core';
import {ViewChild, ElementRef} from '@angular/core';


@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App implements OnInit {
  protected title = 'chatGptClone-frontend';
  private cdRef = inject(ChangeDetectorRef);
  @ViewChild('chatBox') chatBoxRef!: ElementRef

  // Historique des messages (user + bot)
  messages = [
    { sender: 'bot', text: 'Bonjour ! Je suis votre assistant. Posez-moi une question.' }
  ];

  // Formulaire de saisie
  chatFormGroup!: FormGroup;

  // Saisie utilisateur
  querry = '';

  // Message d’erreur
  errorMessage!: string;

  private fb = inject(FormBuilder);
  private aiService = inject(Ai);

  ngOnInit(): void {
    this.chatFormGroup = this.fb.group({
      querry: this.fb.control('')
    });
  }

  // Envoie la requête utilisateur et affiche la réponse mot par mot
  sendQuerry() {
    const query = this.chatFormGroup?.value.querry;
    if (!query || !query.trim()) return;

    // Affiche le message utilisateur
    this.messages.push({ sender: 'user', text: query });

    const botMessage = { sender: 'bot', text: '' };
    this.messages.push(botMessage);

    fetch(`http://localhost:8080/stream?query=${encodeURIComponent(query)}`)
      .then(response => {
        if (!response.body) throw new Error('Pas de flux reçu depuis le backend');
        const reader = response.body.getReader();
        const decoder = new TextDecoder('utf-8');

        const read = () => {
          reader.read().then(({ done, value }) => {
            if (done) return;
            const chunk = decoder.decode(value, { stream: true });
            botMessage.text += chunk;
            this.cdRef.detectChanges();
            this.scrollToBottom();
            read();
          }).catch(err => {
            this.errorMessage = 'Erreur lors de la lecture du flux : ' + err.message;
          });
        };

        read();
      })
      .catch(err => {
        this.errorMessage = 'Erreur API : ' + err.message;
      });

    this.chatFormGroup.reset(); // ✅ remet le champ à vide
  }

  scrollToBottom(){
    setTimeout(()=>{
      const el= this.chatBoxRef?.nativeElement;
      el.scrollTop=el.scrollHeight
    })
  }

}

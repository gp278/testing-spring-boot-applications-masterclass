package de.rieckpil.courses.book.management;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookSynchronizationListenerTest {

  private static final String VALID_ISBN = "1234567891234";

  @Mock private BookRepository bookRepository;

  @Mock private OpenLibraryApiClient openLibraryApiClient;

  @InjectMocks private BookSynchronizationListener cut;

  @Captor private ArgumentCaptor<Book> bookArgumentCaptor;

  @Test
  void shouldRejectBookWhenIsbnIsMalformed() {
    final String invalidIsbn = "123456789012";
    BookSynchronization bookSynchronization = new  BookSynchronization(invalidIsbn);
    cut.consumeBookUpdates(bookSynchronization);
    verifyNoInteractions(openLibraryApiClient, bookRepository);
  }

  @Test
  void shouldNotOverrideWhenBookAlreadyExists() {
    when(bookRepository.findByIsbn(VALID_ISBN)).thenReturn(new  Book());
    BookSynchronization bookSynchronization = new BookSynchronization(VALID_ISBN);
    cut.consumeBookUpdates(bookSynchronization);
    verify(bookRepository, never()).save(any());
    verifyNoInteractions(openLibraryApiClient);
  }

  @Test
  void shouldThrowExceptionWhenProcessingFails() {
    BookSynchronization bookSynchronization = new BookSynchronization(VALID_ISBN);
    when(bookRepository.findByIsbn(VALID_ISBN)).thenReturn(null);
    when(openLibraryApiClient.fetchMetadataForBook(VALID_ISBN)).thenThrow(new RuntimeException("Network Timeout"));
    assertThrows(RuntimeException.class, () -> cut.consumeBookUpdates(bookSynchronization));
  }

  @Test
  void shouldStoreBookWhenNewAndCorrectIsbn() {
    Book book = new Book();
    book.setIsbn(VALID_ISBN);
    book.setTitle("Java Book");
    when(bookRepository.findByIsbn(VALID_ISBN)).thenReturn(null);
    when(openLibraryApiClient.fetchMetadataForBook(VALID_ISBN)).thenReturn(book);
    BookSynchronization bookSynchronization = new  BookSynchronization(VALID_ISBN);

    cut.consumeBookUpdates(bookSynchronization);

    Mockito.verify(openLibraryApiClient, times(1)).fetchMetadataForBook(VALID_ISBN);
    Mockito.verify(bookRepository, times(1)).save(bookArgumentCaptor.capture());
    verifyNoMoreInteractions(openLibraryApiClient, bookRepository);
    assertEquals("Java Book", bookArgumentCaptor.getValue().getTitle());
    assertEquals(VALID_ISBN, bookArgumentCaptor.getValue().getIsbn());
  }
}

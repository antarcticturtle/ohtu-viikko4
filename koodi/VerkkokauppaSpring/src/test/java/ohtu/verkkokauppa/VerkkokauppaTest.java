package ohtu.verkkokauppa;

import ohtu.verkkokauppa.Kauppa;
import ohtu.verkkokauppa.Kirjanpito;
import ohtu.verkkokauppa.Ostoskori;
import ohtu.verkkokauppa.Pankki;
import ohtu.verkkokauppa.Tuote;
import ohtu.verkkokauppa.Varasto;
import ohtu.verkkokauppa.Viitegeneraattori;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Mockito.*;

public class VerkkokauppaTest {
    
    
    Kauppa kauppa;
    //Kirjanpito kirjanpito;
    //Ostoskori ostoskori;
    Pankki pankki;
    Varasto varasto;
    Viitegeneraattori vg;
            
    
    @Before
    public void setUp(){
        pankki = mock(Pankki.class);
        
        vg = mock(Viitegeneraattori.class);
        when(vg.uusi()).thenReturn(42);
        
        varasto = mock(Varasto.class);
        when(varasto.saldo(1)).thenReturn(10); 
        when(varasto.saldo(2)).thenReturn(10);
        when(varasto.saldo(3)).thenReturn(0);
        when(varasto.haeTuote(1)).thenReturn(new Tuote(1, "maito", 5));
        when(varasto.haeTuote(2)).thenReturn(new Tuote(2, "sokeri", 2));
        when(varasto.haeTuote(3)).thenReturn(new Tuote(3, "loppu", 8));
        
        kauppa = new Kauppa(varasto, pankki, vg);
        
    }
    
    @Test
    public void ostoksenPaaytyttyaPankinMetodiaTilisiirtoKutsutaanOikeillaParametreilla(){
        kauppa.aloitaAsiointi();
        kauppa.lisaaKoriin(1);
        kauppa.tilimaksu("Anna", "1234567");
        
        verify(pankki).tilisiirto(eq("Anna"), eq(42), eq("1234567"), anyString(), eq(5));
        verify(vg, times(1)).uusi();
    }
    
    @Test
    public void KaksiEriOstostaOikeaTilisiirto(){
        kauppa.aloitaAsiointi();
        kauppa.lisaaKoriin(1);
        kauppa.lisaaKoriin(2);
        kauppa.tilimaksu("Anna", "1234567");
        
        verify(pankki).tilisiirto(eq("Anna"), eq(42), eq("1234567"), anyString(), eq(7));
        verify(vg, times(1)).uusi();
    }
    
    @Test
    public void KaksiSamaaOstostaOikeaTilisiirto(){
        kauppa.aloitaAsiointi();
        kauppa.lisaaKoriin(1);
        kauppa.lisaaKoriin(1);
        kauppa.tilimaksu("Anna", "1234567");
        
        verify(pankki).tilisiirto(eq("Anna"), eq(42), eq("1234567"), anyString(), eq(10));
        verify(vg, times(1)).uusi();
    }
    
    @Test
    public void KaksiEriOstostaToinenLoppuOikeaTilisiirto(){
        kauppa.aloitaAsiointi();
        kauppa.lisaaKoriin(1);
        kauppa.lisaaKoriin(3);
        kauppa.tilimaksu("Anna", "1234567");
        
        verify(pankki).tilisiirto(eq("Anna"), eq(42), eq("1234567"), anyString(), eq(5));
        verify(vg, times(1)).uusi();
    }
    
    @Test
    public void PoistaKorista(){
        kauppa.aloitaAsiointi();
        kauppa.lisaaKoriin(1);
        kauppa.poistaKorista(1);
        
        verify(varasto, times(2)).haeTuote(anyInt());
        
        Tuote t = varasto.haeTuote(1);
        verify(varasto).palautaVarastoon(t);
    }
    
    
    
}

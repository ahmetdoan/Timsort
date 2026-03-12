# Timsort — Java Implementasyonu

Java ile sıfırdan yazılmış Timsort algoritması ve zaman karmaşıklığı karşılaştırması.
## Amaç

Bu proje Timsort algoritmasını anlamak ve sıfırdan implement etmek amacıyla yazılmıştır. 
Amaç, algoritmanın çalışma prensiplerini öğrenmek ve farklı veri tiplerinde performansını incelemektir.

---

## Timsort Nedir?

Timsort 2002'de Tim Peters tarafından Python için geliştirilmiş hibrit bir sıralama algoritmasıdır.  
**Insertion** Sort ve **Merge Sort**'u bir arada kullanır. Gerçek hayattaki verilerin çoğunun kısmen sıralı olduğu fikrinden yola çıkılarak bu durumu artıya çevirecek bir algoritma tasarlanmıştır.

### Nerede kullanılır?
- **Python** — list.sort() ve sorted() fonksiyonları Timsort ile çalışır.
- **Java** — Arrays.sort() ve Collections.sort() Timsort kullanır.

---

## Algoritma Adımları

### 1. Run Tespiti

Önce dizi içindeki hali hazırda sıralı olan parçalar **(run)** bulur.

- Eğer run **azalan** sıradaysa, ters çevrilir → artan hale getirilir.
- Eğer run **artan** sıradaysa, olduğu gibi bırakılır.


### 2. minRun Hesaplama

Her run'ın olabileceği en küçük boyut minRun ile belirlenir.  
`minRun`, dizinin boyutuna bağlı olarak **32–64** arasında seçilir.

- Tespit edilen run bu değerden kısa olduğu durumda, **Insertion Sort** ile `minRun` boyutuna büyütülür.
- Insertion Sort küçük dizilerde iyi çalıştığı için bu adım hızlıdır.

### 3. Merge Etme

Run'lar stack'e eklenir. Her push sonrası `mergeCollapse` methodu çağrılır.  
Stack'teki son 3 run A, B, C kontrol edilir:

```
A > B + C   ve   B > C   →  invariant sağlıyorsa merge yapılmaz
```

Bu kuralların ihlal edildiği durumda bitişik run'lar merge edilir. Tüm run'lar bittiğinde `mergeForceCollapse` ile geri kalan her run merge edilir.

---

## Neden Insertion Sort  Merge Sort Bir Arada kullanıyoruz?

| Durum | Kullanılan Algoritma |
|---|---|
| Küçük run'ı büyütme | Insertion Sort |
| Run'ları birleştirme | Merge Sort |

Küçük parçalarda Merge Sort daha maliyetli; Insertion Sort küöük veride daha hızlı.  
Büyük run'ların birleştirirken Merge Sort kullanılır. Çünkü O(n log n) garantisi var.

---

## Kısmen Sıralı Veride Timsort

Timsort'un en güçlü olduğu durum **neredeyse sıralı** dizilerdir.

- Run'lar uzun olursa merge sayısı düşer.
- Kısmen sıralı dizide run sayısı düşük olur.
- Zaten sıralı bir dizide Timsort **O(n)'e** yakın çalışır.

Benchmark sonuçlarında bu netçe gözükmektedir (sonuçlar için aşağıya bkz.).

---

## Galloping Modu (Eklenmedi)

Gerçek Timsort implementasyonunda bir run'ları merge ederken, bir run'daki elemanlar diğerinden sürekli az 
ve ya fazla geliyorsa, algoritma **galloping** ile binary search kullanarak toplu kopyalama yapar.  
Bu implementasyonda galloping eklenmemiştir, ileri versiyonlarda eklenebilir.

---

## Nasıl Yazdım?

Proje iki ana dosyadan oluşuyor:

### `Timsort.java`

| Metot | Görev |
|---|---|
| `minRunLen` | Dizi boyutuna göre minRun hesaplar |
| `countRunAndMakeAscending` | Run tespit eder, gerekirse ters çevirir |
| `insertionSort` | Belirtilen aralıktaki verileri insertion sort ile sıralar |
| `reverseInRange` | Belirtilen aralıktaki verileriters çevirir |
| `mergeCollapse` | Her push sonrası stack'i kontrol eder |
| `mergeForceCollapse` | Kalan bütün run'ları birleştirir |
| `mergeAt` | İki run'ı merge sort ile birleştirir |

`RunStack` sınıfı, run'ların başlangıç indexlerini ve uzunluklarını takip eden bir stack.

### `TimsortBenchmark.java`

- İlk 20 run JVM warm-up için hesaba katılmaz, kalan 80 run ile Max/Min/Ortalama bulunur.
- **Insertion Sort vs Timsort** karşılaştırması: 10.000 ve 100.000 boyutlu rastgele dizi.
- **Timsort unsorted vs mostly sorted** karşılaştırması: 500.000 boyutlu dizi.

---

## Benchmark Output

(Tüm süre uzunlukları milisaniye cinsine çevrilmiştir.)

### 10.000 Eleman — Insertion Sort vs Timsort

| | Maks | Min | Ortalama |
|---|---|---|---|
| Insertion Sort | 76.27 ms | 17.62 ms | 37.28 ms |
| Timsort | 1.75 ms | 0.59 ms | **0.93 ms** |

Timsort, 10.000 elemanda Insertion Sort'a göre ortalamada yaklaşık **40x daha hızlı**.

---

### 100.000 Eleman — Insertion Sort vs Timsort

| | Maks | Min | Ortalama |
|---|---|---|---|
| Insertion Sort | 5467.05 ms | 1771.19 ms | 3602.01 ms |
| Timsort | 29.23 ms | 7.63 ms | **14.44 ms** |

Timsort 100.000 elemanda yaklaşık **250x daha hızlı**.  
boyut 10x artınca Insertion Sort süresi 97x artmış → O(n²) davranışı
Timsort süresi ise 8x artmış → O(n log n) ile uyumlu

---

### 500.000 Eleman — Timsort: Sırasız vs Nerdeyse Sıralı

| | Maks | Min | Ortalama |
|---|---|---|---|
| Timsort (sırasız) | 130.90 ms | 44.26 ms | 83.04 ms |
| Timsort (%99 sıralı) | 52.24 ms | 11.78 ms | **24.80 ms** |

%99 sıralı dizide Timsort, tamamen sırasız diziye kıyasla ortalamada **3.3x daha hızlı**.  
Run'lar uzun olduğunda merge sayısı ciddi biçimde azalıyor.

---

## Zaman Karmaşıklığı

| Durum | Karmaşıklık |
|---|---|
| En kötü | O(n log n) |
| Ortalama | O(n log n) |
| En iyi (sıralı dizi) | O(n) |
| Bellek | O(n) |

En iyi durum, tüm dizi tek bir run olur ve sıfır merge yapılır.

---

## Çalıştırma

```
Timsort/
├── README.md
└── src/
    └── timsort/
        ├── Timsort.java
        └── TimsortBenchmark.java
```

`Timsort.java` → `main` içindeki basit test ile bir adet dizi sıralanmıştır. Burdan doğrulama yapılabilir.  
`TimsortBenchmark.java` → `main` çalıştırılarak benchmark sonuçları elde edilir (sonuç almak yaklaşık 5-10 dakika).

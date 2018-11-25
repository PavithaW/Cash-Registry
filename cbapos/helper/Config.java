package com.cbasolutions.cbapos.helper;
import android.content.Context;
import android.content.SharedPreferences;
import android.telephony.TelephonyManager;

import com.cbasolutions.cbapos.model.Payment;
import com.cbasolutions.cbapos.model.Transaction;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Tharaka on 10/26/15.
 */
public class Config {

    static Pattern pattern;
    static Matcher matcher;
    public static final int DEFAULT_TIMEOUT = 300 * 1000;

    public static final String SERVICE_URL = "http://123.231.14.207:81/cashRegistry/api/";
    // suba
    //public static final String SERVICE_URL = "http://192.168.2.241:81/cashRegistry/api/";
    public static final String ENVIRONMENT = "dev"; // "dev" for Development, "qa" for QA

    public static final String LOGIN = "login";
    public static final String SIGN_UP = "signup";
    public static final String FORGET_PASSWORD = "forgot";
    public static final String CANCEL_TRANSACTION = "CancelTransaction";
    public static final String SEND_RECEIPT ="receipt";
    public static final String SEND_SMS ="sendsms";
    public static final String LOW_STOCK ="lowstockalerts";

    public static final int CARD = 0;
    public static final int CASH = 1;
    public static final int REFUND = 2;

    public static final String DISCOUNT = "DISCOUNT";
    public static final String CATEGORY = "CATEGORY";
    public static final String ITEM = "ITEM";
    public static final String CUSTOM = "CUSTOM";

    public static final String REFUND_REASON_1 = "Accidental Charge";
    public static final String REFUND_REASON_2 = "Refunded Goods";
    public static final String REFUND_REASON_3 = "Cancelled Order";

    public static final String APP_NAME = "cba_pos";
    public static final String NO_IMG = null;

    public static final String AUTH_HEADER_KEY = "Portal-Auth";
    public static final String AUTH_HEADER_VALUE = "53C58D8953F9BB2F6A3519E96F8C00B";
    public static final String USER_AGENT_KEY = "User-Agent";
    public static final String USER_AGENT_VALUE = "MPOS-WECHAT-PORTAL";

    public static final int COLOR_1 = 0;
    public static final int COLOR_2 = 1;
    public static final int COLOR_3 = 2;
    public static final int COLOR_4 = 3;
    public static final int COLOR_5 = 4;
    public static final int COLOR_6 = 5;

    public static final int ITEMS_VIEW = 1;
    public static final int CATEGORY_VIEW = 2;
    public static final int DISCOUNT_VIEW = 3;
    public static final int TRANSACTION_VIEW= 4;

    public static final String DISCOUNT_TYPE_1 = "%";
    public static final String DISCOUNT_TYPE_2 = "Rs";

    public static final int SUCCESS = 1;

    //Actions
    public static final String BOOK_ACTION = "book";

    private static final String PREF_NAME = "DEVICE_PREF";
    public static final String KEY_DEVICE_NAME ="device_name";

    public static int VISIBLE_VIEW = 0;
    public static boolean isOnline = false;

    static final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    static SecureRandom rnd = new SecureRandom();
    public static double totalAmount;
    public static double initialAmount;
    public static double splittedAmount;
    public static double remainingAmount;
    public static String tId;
    public static ArrayList<Payment> savedSplitList;
    public static boolean isInCompletePayment = false;
    public static Transaction savedTransaction = null;
    public static boolean dialogIsShowing = false;
    public static boolean catDialogIsShowing = false;
    public static boolean issearching = false;



    public static final String[] CITIES = new String[] {"Addalaichenai", "Akkaraipattu", "Ampara", "Bakmitiyawa", "Central Camp", "Dadayamtalawa", "Damana", "Damanewela", "Deegawapiya", "Dehiattakandiya", "Devalahinda", "Digamadulla Weeragoda", "Dorakumbura", "Galapitagala", "Gonagolla", "Hingurana", "Hulannuge", "Kalmunai", "Kannakipuram", "Karativu", "Kekirihena", "Koknahara", "Kolamanthalawa", "Komari", "Lahugala", "lmkkamam", "Madawalalanda", "Mahanagapura", "Mahaoya", "Malwatta", "Mangalagama", "Marathamune", "Mawanagama", "Moragahapallama", "Namaloya", "Navithanveli", "Nawamedagama", "Nintavur", "Oluvil", "Padiyatalawa", "Pahalalanda", "Palamunai", "Panama", "Pannalagama", "Paragahakele", "Periyaneelavanai", "Polwaga Janapadaya", "Pottuvil", "Rajagalatenna", "Sainthamaruthu", "Samanthurai", "Serankada", "Siripura", "Siyambalawewa", "Tempitiya", "Thambiluvil", "Tirukovil", "Uhana", "Wadinagala", "Wanagamuwa", "Werunketagoda", "Andiyagala", "Angamuwa", "Anuradhapura", "Awukana", "Bogahawewa", "Dematawewa", "Dunumadalawa", "Dutuwewa", "Elayapattuwa", "Eppawala", "Etawatunuwewa", "Etaweeragollewa", "Galadivulwewa", "Galenbindunuwewa", "Galkadawala", "Galkiriyagama", "Galkulama", "Galnewa", "Gambirigaswewa", "Ganewalpola", "Gemunupura", "Getalawa", "Gnanikulama", "Gonahaddenawa", "Habarana", "Halmillawa Dambulla", "Halmillawetiya", "Hidogama", "Horawpatana", "Horiwila", "Hurigaswewa", "Hurulunikawewa", "Kagama", "Kahatagasdigiliya", "Kahatagollewa", "Kalakarambewa", "Kalankuttiya", "Kalaoya", "Kalawedi Ulpotha", "Kallanchiya", "Kapugallawa", "Karagahawewa", "Katiyawa", "Kebithigollewa", "Kekirawa", "Kendewa", "Kiralogama", "Kirigalwewa", "Kitulhitiyawa", "Kurundankulama", "Labunoruwa", "lhala Halmillewa", "lhalagama", "lpologama", "Madatugama", "Maha Elagamuwa", "Mahabulankulama", "Mahailluppallama", "Mahakanadarawa", "Mahapothana", "Mahasenpura", "Mahawilachchiya", "Mailagaswewa", "Malwanagama", "Maneruwa", "Maradankadawala", "Maradankalla", "Medawachchiya", "Megodawewa", "Mihintale", "Morakewa", "Mulkiriyawa", "Muriyakadawala", "Nachchaduwa", "Namalpura", "Negampaha", "Nochchiyagama", "Padavi Maithripura", "Padavi Parakramapura", "Padavi Sripura", "Padavi Sritissapura", "Padaviya", "Padikaramaduwa", "Pahala Halmillewa", "Pahala Maragahawe", "Pahalagama", "Palagala", "Palugaswewa", "Pandukabayapura", "Pandulagama", "Parakumpura", "Parangiyawadiya", "Parasangahawewa", "Pemaduwa", "Perimiyankulama", "Pihimbiyagolewa", "Pubbogama", "Pulmoddai", "Punewa", "Rajanganaya", "Rambewa", "Rampathwila", "Ranorawa", "Rathmalgahawewa", "Saliyapura", "Seeppukulama", "Senapura", "Sivalakulama", "Siyambalewa", "Sravasthipura", "Talawa", "Tambuttegama", "Tammennawa", "Tantirimale", "Telhiriyawa", "Tennamarawadiya", "Tirappane", "Tittagonewa", "Udunuwara Colony", "Upuldeniya", "Uttimaduwa", "Viharapalugama", "Vijithapura", "Wahalkada", "Wahamalgollewa", "Walagambahuwa", "Walahaviddawewa", "Welimuwapotana", "Welioya Project", "Akkarasiyaya", "Aluketiyawa", "Aluttaramma", "Ambadandegama", "Ambagahawatta", "Ambagasdowa", "Amunumulla", "Arawa", "Arawakumbura", "Arawatta", "Atakiriya", "Badulla", "Baduluoya", "Ballaketuwa", "Bambarapana", "Bandarawela", "Beramada", "Bibilegama", "Bogahakumbura", "Boragas", "Boralanda", "Bowela", "Dambana", "Demodara", "Diganatenna", "Dikkapitiya", "Dimbulana", "Divulapelessa", "Diyatalawa", "Dulgolla", "Egodawela", "Ella", "Ettampitiya", "Galauda", "Galedanda", "Galporuyaya", "Gamewela", "Gawarawela", "Girandurukotte", "Godunna", "Gurutalawa", "Haldummulla", "Hali Ela", "Hangunnawa", "Haputale", "Hebarawa", "Heeloya", "Helahalpe", "Helapupula", "Hewanakumbura", "Hingurukaduwa", "Hopton", "Idalgashinna", "Jangulla", "Kahataruppa", "Kalubululanda", "Kalugahakandura", "Kalupahana", "Kandaketya", "Kandegedara", "Kandepuhulpola", "Kebillawela", "Kendagolla", "Keppetipola", "Keselpotha", "Ketawatta", "Kiriwanagama", "Koslanda", "Kotamuduna", "Kuruwitenna", "Kuttiyagolla", "Landewela", "Liyangahawela", "Lunugala", "Lunuwatta", "Madulsima", "Mahiyanganaya", "Makulella", "Malgoda", "Maliyadda", "Mapakadawewa", "Maspanna", "Maussagolla", "Medawela Udukinda", "Medawelagama", "Meegahakiula", "Metigahatenna", "Mirahawatta", "Miriyabedda", "Miyanakandura", "Namunukula", "Narangala", "Nelumgama", "Nikapotha", "Nugatalawa", "Ohiya", "Pahalarathkinda", "Pallekiruwa", "Passara", "Pathanewatta", "Pattiyagedara", "Pelagahatenna", "Perawella", "Pitamaruwa", "Pitapola", "Puhulpola", "Ratkarawwa", "Ridimaliyadda", "Rilpola", "Silmiyapura", "Sirimalgoda", "Sorabora Colony", "Soragune", "Soranatota", "Spring Valley", "Taldena", "Tennepanguwa", "Timbirigaspitiya", "Uduhawara", "Uraniya", "Uva Deegalla", "Uva Karandagolla", "Uva Mawelagama", "Uva Tenna", "Uva Tissapura", "Uva Uduwara", "Uvaparanagama", "Welimada", "Wewatta", "Wineethagama", "Yalagamuwa", "Yalwela", "Ampilanthurai", "Araipattai", "Ayithiyamalai", "Bakiella", "Batticaloa", "Cheddipalayam", "Chenkaladi", "Eravur", "Kalkudah", "Kallar", "Kaluwanchikudi", "Kaluwankemy", "Kannankudah", "Karadiyanaru", "Kathiraveli", "Kattankudi", "Kiran", "Kirankulam", "Koddaikallar", "Kokkaddichcholai", "Kurukkalmadam", "Mandur", "Mankemi", "Miravodai", "Murakottanchanai", "Navagirinagar", "Navatkadu", "Oddamavadi", "Panichankemi", "Pankudavely", "Periyaporativu", "Periyapullumalai", "Pillaiyaradi", "Punanai", "Puthukudiyiruppu", "Thannamunai", "Thettativu", "Thikkodai", "Thirupalugamam", "Thuraineelavanai", "Unnichchai", "Vakaneri", "Vakarai", "Valaichenai", "Vantharumoolai", "Vellavely", "Akarawita", "Angoda", "Arangala", "Athurugiriya", "Avissawella", "Bambalapitiya", "Batawala", "Battaramulla", "Batugampola", "Bope", "Boralesgamuwa", "Borella", "Colombo 01", "Colombo 02", "Colombo 03", "Colombo 04", "Colombo 05", "Colombo 06", "Colombo 07", "Colombo 08", "Colombo 09", "Colombo 10", "Colombo 11", "Colombo 12", "Colombo 13", "Colombo 14", "Colombo 15", "Dedigamuwa", "Dehiwala", "Deltara", "Embuldeniya", "Gongodawila", "Habarakada", "Handapangoda", "Hanwella", "Hewainna", "Hiripitya", "Hokandara", "Homagama", "Horagala", "Kaduwela", "Kahawala", "Kalatuwawa", "Kalubowila", "Kiriwattuduwa", "Kohuwala", "Kolonnawa", "Kosgama", "Kotahena", "Kotikawatta", "Kottawa", "Madapatha", "Maharagama", "Malabe", "Meegoda", "Moratuwa", "Mount Lavinia", "Mullegama", "Mulleriyawa", "Mutwal", "Napawela", "Narahenpita", "Nugegoda", "Padukka", "Pannipitiya", "Piliyandala", "Pita Kotte", "Pitipana Homagama", "Polgasowita", "Puwakpitiya", "Rajagiriya", "Ranala", "Ratmalana", "Siddamulla", "Sri Jayewardenepura", "Talawatugoda", "Tummodara", "Waga", "Watareka", "Wijerama", "Agaliya", "Ahangama", "Ahungalla", "Akmeemana", "Aluthwala", "Ambalangoda", "Ampegama", "Amugoda", "Anangoda", "Angulugaha", "Ankokkawala", "Baddegama", "Balapitiya", "Banagala", "Batapola", "Bentota", "Boossa", "Dikkumbura", "Dodanduwa", "Ella Tanabaddegama", "Elpitiya", "Ethkandura", "Galle", "Ganegoda", "Ginimellagaha", "Gintota", "Godahena", "Gonagalpura", "Gonamulla Junction", "Gonapinuwala", "Habaraduwa", "Haburugala", "Halvitigala Colony", "Hawpe", "Hikkaduwa", "Hiniduma", "Hiyare", "Ihala Walpola", "Kahaduwa", "Kahawa", "Kananke Bazaar", "Karagoda", "Karandeniya", "Kosgoda", "Kottawagama", "Kuleegoda", "lhalahewessa", "lmaduwa", "lnduruwa", "Magedara", "Malgalla Talangalla", "Mapalagama", "Mapalagama Central", "Mattaka", "Meda-Keembiya", "Meetiyagoda", "Miriswatta", "Nagoda", "Nakiyadeniya", "Nawadagala", "Neluwa", "Nindana", "Opatha", "Panangala", "Pannimulla Panagoda", "Parana ThanaYamgoda", "Pitigala", "Poddala", "Porawagama", "Rantotuwila", "Ratgama", "Talagampola", "Talgaspe", "Talgaswela", "Talpe", "Tawalama", "Tiranagama", "Udalamatta", "Udugama", "Uluvitike", "Unawatuna", "Unenwitiya", "Uragaha", "Uragasmanhandiya", "Wakwella", "Walahanduwa", "Wanchawela", "Wanduramba", "Warukandeniya", "Watugedara", "Weihena", "Yakkalamulla", "Yatalamatta", "Akaragama", "Alawala", "Ambagaspitiya", "Ambepussa", "Andiambalama", "Attanagalla", "Badalgama", "Banduragoda", "Batuwatta", "Bemmulla", "Biyagama", "Biyagama IPZ", "Bokalagama", "Bopagama", "Buthpitiya", "Dagonna", "Danowita", "Debahera", "Dekatana", "Delgoda", "Delwagura", "Demalagama", "Demanhandiya", "Dewalapola", "Divulapitiya", "Divuldeniya", "Dompe", "Dunagaha", "Ekala", "Ellakkala", "Essella", "Gampaha", "Ganemulla", "GonawalaWP", "Heiyanthuduwa", "Hendala", "Henegama", "Hinatiyana Madawala", "Hiswella", "Horampella", "Hunumulla", "Ihala Madampella", "Imbulgoda", "Ja-Ela", "Kadawatha", "Kahatowita", "Kalagedihena", "Kaleliya", "Kaluaggala", "Kandana", "Kapugoda", "Kapuwatta", "Katana", "Katunayake", "Katunayake Air Force Camp", "Katuwellegama", "Kelaniya", "Kimbulapitiya", "Kiribathgoda", "Kirindiwela", "Kitalawalana", "Kitulwala", "Kochchikade", "Kotadeniyawa", "Kotugoda", "Kumbaloluwa", "Loluwagoda", "Lunugama", "Mabodale", "Madelgamuwa", "Makewita", "Makola", "Malwana", "Mandawala", "Marandagahamula", "Mellawagedara", "Minuwangoda", "Mirigama", "Mithirigala", "Muddaragama", "Mudungoda", "Naranwala", "Nawana", "Nedungamuwa", "Negombo", "Nikahetikanda", "Nittambuwa", "Niwandama", "Pallewela", "Pamunugama", "Pamunuwatta", "Pasyala", "Peliyagoda", "Pepiliyawala", "Pethiyagoda", "Polpithimukulana", "Pugoda", "Radawadunna", "Radawana", "Raddolugama", "Ragama", "Ruggahawila", "Rukmale", "Seeduwa", "Siyambalape", "Talahena", "Thimbirigaskatuwa", "Tittapattara", "Udathuthiripitiya", "Udugampola", "Uggalboda", "Urapola", "Uswetakeiyawa", "Veyangoda", "Walgammulla", "Walpita", "Wanaluwewa", "Wathurugama", "Watinapaha", "Wattala", "Weboda", "Wegowwa", "Weliveriya", "Weweldeniya", "Yakkala", "Ambalantota", "Angunakolapelessa", "Bandagiriya Colony", "Barawakumbuka", "Beliatta", "Beragama", "Beralihela", "Bowalagama", "Bundala", "Ellagala", "Gangulandeniya", "Getamanna", "Goda Koggalla", "Gonagamuwa Uduwila", "Gonnoruwa", "Hakuruwela", "Hambantota", "Horewelagoda", "Hungama", "Ihala Beligalla", "Ittademaliya", "Julampitiya", "Kahandamodara", "Kariyamaditta", "Katuwana", "Kawantissapura", "Kirama", "Kirinda", "Lunama", "Lunugamwehera", "Magama", "Mahagalwewa", "Mamadala", "Medamulana", "Middeniya", "Migahajandur", "Modarawana", "Mulkirigala", "Nakulugamuwa", "Netolpitiya", "Nihiluwa", "Padawkema", "Pahala Andarawewa", "Pallekanda", "Rammalawarapitiya", "Ranakeliya", "Ranmuduwewa", "Ranna", "Ratmalwala", "RU/Ridiyagama", "Sooriyawewa Town", "Tangalla", "Tissamaharama", "Uda Gomadiya", "Udamattala", "Uswewa", "Vitharandeniya", "Walasmulla", "Weeraketiya", "Weerawila", "Weerawila NewTown", "Wekandawela", "Weligatta", "Yatigala", "Allaipiddi", "Allaveddi", "Alvai", "Anaicoddai", "Analaitivu", "Atchuveli", "Chankanai", "Chavakachcheri", "Chullipuram", "Chundikuli", "Chunnakam", "Delft", "DelftWest", "Eluvaitivu", "Erlalai", "Jaffna", "Kaitadi", "Kankesanthurai", "Karainagar", "Karaveddi", "Kayts", "Kodikamam", "Kokuvil", "Kondavil", "Kopay", "Kudatanai", "llavalai", "Mallakam", "Manipay", "Mathagal", "Meesalai", "Mirusuvil", "Nagar Kovil", "Nainathivu", "Neervely", "Pandaterippu", "Point Pedro", "Pungudutivu", "Putur", "Sandilipay", "Sithankemy", "Tellippallai", "Thondamanaru", "Urumpirai", "Vaddukoddai", "Valvettithurai", "Vannarponnai", "Varany", "Vasavilan", "Velanai", "Agalawatta", "Alubomulla", "Alutgama", "Anguruwatota", "Baduraliya", "Bandaragama", "Bellana", "Beruwala", "Bolossagama", "Bombuwala", "Boralugoda", "Bulathsinhala", "Danawala Thiniyawala", "Delmella", "Dharga Town", "Diwalakada", "Dodangoda", "Dombagoda", "Galpatha", "Gamagoda", "Gonapola Junction", "Govinna", "Gurulubadda", "Halkandawila", "Haltota", "Halwala", "Halwatura", "Hedigalla Colony", "Horana", "Ittapana", "Kalawila Kiranthidiya", "Kalutara", "Kananwila", "Kandanagama", "Kehelwatta", "Kelinkanda", "Kitulgoda", "Koholana", "Kuda Uduwa", "lngiriya", "Maggona", "Mahagama", "Mahakalupahana", "Matugama", "Meegahatenna", "Meegama", "Millaniya", "Millewa", "Miwanapalana", "Molkawa", "Morapitiya", "Morontuduwa", "Nawattuduwa", "Neboda", "Padagoda", "Pahalahewessa", "Paiyagala", "Panadura", "Pannila", "Paragastota", "Paragoda", "Paraigama", "Pelanda", "Pelawatta", "Pokunuwita", "Polgampola", "Poruwedanda", "Remunagoda", "Tebuwana", "Uduwara", "Utumgama", "Veyangalla", "Wadduwa", "Walagedara", "Walallawita", "Waskaduwa", "Welipenna", "Welmilla Junction", "Yagirala", "Yatadolawatta", "Yatawara Junction", "Akurana", "Alawatugoda", "Aludeniya", "Ambagahapelessa", "Ambatenna", "Ampitiya", "Ankumbura", "Atabage", "Balana", "Bambaragahaela", "Barawardhana Oya", "Batagolladeniya", "Batugoda", "Batumulla", "Bawlana", "Bopana", "Danture", "Dedunupitiya", "Dekinda", "Deltota", "Dolapihilla", "Dolosbage", "Doluwa", "Doragamuwa", "Dunuwila", "Ekiriya", "Elamulla", "Etulgama", "Galaboda", "Galagedara", "Galaha", "Galhinna", "Gallellagama", "Gampola", "Gelioya", "Godamunna", "Gomagoda", "Gonagantenna", "Gonawalapatana", "Gunnepana", "Gurudeniya", "Halloluwa", "Handaganawa", "Handawalapitiya", "Handessa", "Hanguranketha", "Harankahawa", "Hasalaka", "Hataraliyadda", "Hewaheta", "Hindagala", "Hondiyadeniya", "Hunnasgiriya", "Jambugahapitiya", "Kadugannawa", "Kahataliyadda", "Kalugala", "Kandy", "Kapuliyadde", "Karandagolla", "Katugastota", "Kengalla", "Ketakumbura", "Ketawala Leula", "Kiribathkumbura", "Kobonila", "Kolabissa", "Kolongoda", "Kulugammana", "Kumbukkandura", "Kumburegama", "Kundasale", "Leemagahakotuwa", "lhala Kobbekaduwa", "lllagolla", "Lunuketiya Maditta", "Madawala Bazaar", "Madugalla", "Madulkele", "Mahadoraliyadda", "Mahamedagama", "Mailapitiya", "Makkanigama", "Makuldeniya", "Mandaram Nuwara", "Mapakanda", "Marassana", "Marymount Colony", "Maturata", "Mawatura", "Medamahanuwara", "Medawala Harispattuwa", "Meetalawa", "Megoda Kalugamuwa", "Menikdiwela", "Menikhinna", "Mimure", "Minigamuwa", "Minipe", "Murutalawa", "Muruthagahamulla", "Naranpanawa", "Nattarampotha", "Nawalapitiya", "Nillambe", "Nugaliyadda", "Nugawela", "Pallebowala", "Pallekotuwa", "Panvila", "Panwilatenna", "Paradeka", "Pasbage", "Pattitalawa", "Pattiya Watta", "Penideniya", "Peradeniya", "Pilawala", "Pilimatalawa", "Poholiyadda", "Polgolla", "Pujapitiya", "Pupuressa", "Pussellawa", "Putuhapuwa", "Rajawella", "Rambukpitiya", "Rambukwella", "Rangala", "Rantembe", "Rathukohodigala", "Rikillagaskada", "Sangarajapura", "Senarathwela", "Talatuoya", "Tawalantenna", "Teldeniya", "Tennekumbura", "Uda Peradeniya", "Udahentenna", "Udahingulwala", "Udatalawinna", "Udawatta", "Udispattuwa", "Ududumbara", "Uduwa", "Uduwahinna", "Uduwela", "Ulapane", "Ulpothagama", "Unuwinna", "Velamboda", "Watagoda Harispattuwa", "Wattappola", "Wattegama", "Weligalla", "Weligampola", "Wendaruwa", "Weragantota", "Werapitya", "Werellagama", "Wettawa", "Wilanagama", "Yahalatenna", "Yatihalagala", "Alawatura", "Algama", "Alutnuwara", "Ambalakanda", "Ambulugala", "Amitirigala", "Ampagala", "Anhettigama", "Aranayaka", "Aruggammana", "Atale", "Batuwita", "Berannawa", "Boralankada", "Bossella", "Bulathkohupitiya", "Damunupola", "Debathgama", "Dedugala", "Deewala Pallegama", "Dehiowita", "Deldeniya", "Deloluwa", "Deraniyagala", "Dewalegama", "Dewanagala", "Dombemada", "Dorawaka", "Dunumala", "Galapitamada", "Galatara", "Galigamuwa Town", "Gantuna", "Gonagala", "Hakahinna", "Hakbellawaka", "Helamada", "Hemmatagama", "Hettimulla", "Hewadiwela", "Hingula", "Hinguralakanda", "Hiriwadunna", "Imbulana", "Imbulgasdeniya", "Kabagamuwa", "Kannattota", "Kegalle", "Kehelpannala", "Kitulgala", "Kondeniya", "Kotiyakumbura", "Lewangama", "Mahabage", "Mahapallegama", "Maharangalla", "Makehelwala", "Malalpola", "Maliboda", "Malmaduwa", "Mawanella", "Migastenna Sabara", "Miyanawita", "Molagoda", "Morontota", "Nelundeniya", "Niyadurupola", "Noori", "Parape", "Pattampitiya", "Pitagaldeniya", "Pothukoladeniya", "Rambukkana", "Ruwanwella", "Seaforth Colony", "Talgaspitiya", "Teligama", "Tholangamuwa", "Thotawella", "Tulhiriya", "Tuntota", "Udagaldeniya", "Udapotha", "Udumulla", "Undugoda", "Ussapitiya", "Wahakula", "Waharaka", "Warakapola", "Watura", "Weeoya", "Wegalla", "Welihelatenna", "Weragala", "Yatagama", "Yatapana", "Yatiyantota", "Yattogoda", "Alahengama", "Alahitiyawa", "Alawatuwala", "Alawwa", "Ambakote", "Ambanpola", "Anhandiya", "Anukkane", "Aragoda", "Ataragalla", "Awulegama", "Balalla", "Bamunukotuwa", "Bandara Koswatta", "Bingiriya", "Bogamulla", "Bopitiya", "Boraluwewa", "Boyagane", "Bujjomuwa", "Buluwala", "Dambadeniya", "Daraluwa", "Deegalla", "Delwite", "Demataluwa", "Diddeniya", "Digannewa", "Divullegoda", "Dodangaslanda", "Doratiyawa", "Dummalasuriya", "Ehetuwewa", "Elibichchiya", "Embogama", "Etungahakotuwa", "Galgamuwa", "Gallewa", "Girathalana", "Giriulla", "Gokaralla", "Gonawila", "Halmillawewa", "Hengamuwa", "Hettipola", "Hilogama", "Hindagolla", "Hiriyala Lenawa", "Hiruwalpola", "Horambawa", "Hulogedara", "Hulugalla", "Hunupola", "Ihala Gomugomuwa", "Ihala Katugampala", "Indulgodakanda", "Inguruwatta", "Iriyagolla", "Ithanawatta", "Kadigawa", "Kahapathwala", "Kalugamuwa", "Kanadeniyawala", "Kanattewewa", "Karagahagedara", "Karambe", "Katupota", "Kekunagolla", "Keppitiwalana", "Kimbulwanaoya", "Kirimetiyawa", "Kirindawa", "Kirindigalla", "Kithalawa", "Kobeigane", "Kohilagedara", "Konwewa", "Kosdeniya", "Kosgolla", "Kotawehera", "Kudagalgamuwa", "Kudakatnoruwa", "Kuliyapitiya", "Kumbukgeta", "Kumbukwewa", "Kuratihena", "Kurunegala", "Labbala", "lbbagamuwa", "lhala Kadigamuwa", "llukhena", "Lonahettiya", "Madahapola", "Madakumburumulla", "Maduragoda", "Maeliya", "Magulagama", "Mahagalkadawala", "Mahagirilla", "Mahamukalanyaya", "Mahananneriya", "Maharachchimulla", "Maho", "Makulewa", "Makulpotha", "Makulwewa", "Malagane", "Mandapola", "Maspotha", "Mawathagama", "Medivawa", "Meegalawa", "Meetanwala", "Meewellawa", "Melsiripura", "Metikumbura", "Metiyagane", "Minhettiya", "Minuwangete", "Mirihanagama", "Monnekulama", "Moragane", "Moragollagama", "Morathiha", "Munamaldeniya", "Muruthenge", "Nabadewa", "Nagollagama", "Nagollagoda", "Nakkawatta", "Narammala", "Narangoda", "Nawatalwatta", "Nelliya", "Nikadalupotha", "Nikaweratiya", "Padeniya", "Padiwela", "Pahalagiribawa", "Pahamune", "Palukadawala", "Panadaragama", "Panagamuwa", "Panaliya", "Panliyadda", "Pannala", "Pansiyagama", "Periyakadneluwa", "Pihimbiya Ratmale", "Pihimbuwa", "Pilessa", "Polgahawela", "Polpitigama", "Pothuhera", "Puswelitenna", "Ridibendiella", "Ridigama", "Saliya Asokapura", "Sandalankawa", "Sirisetagama", "Siyambalangamuwa", "Solepura", "Solewewa", "Sunandapura", "Talawattegedara", "Tambutta", "Thalahitimulla", "Thalakolawewa", "Thalwita", "Thambagalla", "Tharana Udawela", "Thimbiriyawa", "Tisogama", "Torayaya", "Tuttiripitigama", "Udubaddawa", "Uhumiya", "Ulpotha Pallekele", "Usgala Siyabmalangamuwa", "Wadakada", "Wadumunnegedara", "Walakumburumulla", "Wannigama", "Wannikudawewa", "Wannilhalagama", "Wannirasnayakapura", "Warawewa", "Wariyapola", "Watuwatta", "Weerapokuna", "Welawa Juncton", "Welipennagahamulla", "Wellagala", "Wellarawa", "Wellawa", "Welpalla", "Wennoruwa", "Weuda", "Wewagama", "Yakwila", "Yatigaloluwa", "Adampan", "Arippu", "Athimottai", "Chilavathurai", "Erukkalampiddy", "llluppaikadavai", "Madhu Church", "Madhu Road", "Mannar", "Marichchi Kaddi", "Murunkan", "Nanattan", "P.P.Potkemy", "Palampiddy", "Periyakunchikulam", "Periyamadhu", "Pesalai", "Talaimannar", "Temple", "Tharapuram", "Thiruketheeswaram Temple", "Uyilankulam", "Vaddakandal", "Vankalai", "Vellan Kulam", "Vidataltivu", "Akuramboda", "Alwatta", "Ambana", "Ataragallewa", "Bambaragaswewa", "Beligamuwa", "Dambulla", "Dankanda", "Devagiriya", "Dewahuwa", "Dullewa", "Dunkolawatta", "Dunuwilapitiya", "Elkaduwa", "Erawula Junction", "Etanawala", "Galewela", "Gammaduwa", "Gangala Puwakpitiya", "Handungamuwa", "Hattota Amuna", "Imbulgolla", "Inamaluwa", "Kaikawala", "Kalundawa", "Kandalama", "Karagahinna", "Katudeniya", "Kavudupelella", "Kibissa", "Kiwula", "Kongahawela", "Laggala Pallegama", "Leliambe", "Lenadora", "lllukkumbura", "Madawala Ulpotha", "Madipola", "Mahawela", "Mananwatta", "Maraka", "Matale", "Melipitiya", "Metihakka", "Millawana", "Muwandeniya", "Nalanda", "Naula", "Nugagolla", "Opalgala", "Ovilikanda", "Palapathwela", "Pallepola", "Perakanatta", "Pubbiliya", "Ranamuregama", "Rattota", "Selagama", "Sigiriya", "Talagoda Junction", "Talakiriyagama", "Udasgiriya", "Udatenna", "Ukuwela", "Wahacotte", "Walawela", "Wehigala", "Welangahawatte", "Wewalawewa", "Wilgamuwa", "Yatawatta", "Akuressa", "Alapaladeniya", "Aparekka", "Athuraliya", "Bengamuwa", "Beralapanathara", "Bopagoda", "Dampahala", "Deegala Lenama", "Deiyandara", "Dellawa", "Denagama", "Denipitiya", "Deniyaya", "Derangala", "Dikwella", "Diyagaha", "Diyalape", "Gandara", "Godapitiya", "Gomilamawarala", "Hakmana", "Handugala", "Horapawita", "Kalubowitiyana", "Kamburugamuwa", "Kamburupitiya", "Karagoda Uyangoda", "Karaputugala", "Karatota", "Kekanadurra", "Kiriweldola", "Kiriwelkele", "Kolawenigama", "Kotapola", "Kottegoda", "Lankagama", "Makandura", "Maliduwa", "Maramba", "Matara", "Mediripitiya", "Miella", "Mirissa", "Moragala Kirillapone", "Morawaka", "Mulatiyana Junction", "Nadugala", "Naimana", "Narawelpita", "Pahala Millawa", "Palatuwa", "Paragala", "Parapamulla", "Pasgoda", "Penetiyana", "Pitabeddara", "Pothdeniya", "Puhulwella", "Radawela", "Ransegoda", "Ratmale", "Rotumba", "Siyambalagoda", "Sultanagoda", "Telijjawila", "Thihagoda", "Urubokka", "Urugamuwa", "Urumutta", "Viharahena", "Walakanda", "Walasgala", "Waralla", "Weligama", "Wilpita", "Yatiyana", "Angunakolawewa", "Ayiwela", "Badalkumbura", "Baduluwela", "Bakinigahawela", "Balaharuwa", "Bibile", "Buddama", "Buttala", "Dambagalla", "Diyakobala", "Dombagahawela", "Ekamutugama", "Ekiriyankumbura", "Ethimalewewa", "Ettiliwewa", "Galabedda", "Hambegamuwa", "Hulandawa", "Inginiyagala", "Kandaudapanguwa", "Kandawinna", "Kataragama", "Kiriibbanwewa", "Kotagama", "Kotawehera Mankada", "Kotiyagala", "Kumbukkana", "Mahagama Colony", "Marawa", "Mariarawa", "Medagana", "Monaragala", "Moretuwegama", "Nakkala", "Nannapurawa", "Nelliyadda", "Nilgala", "Obbegoda", "Okkampitiya", "Pangura", "Pitakumbura", "Randeniya", "Ruwalwela", "Sella Kataragama", "Sewanagala", "Siyambalagune", "Siyambalanduwa", "Suriara", "Tanamalwila", "Uva Gangodagama", "Uva Kudaoya", "Uva Pelwatta", "Warunagama", "Wedikumbura", "Weherayaya Handapanagala", "Wellawaya", "Wilaoya", "Alampil", "Karuppaddamurippu", "Kokkilai", "Kokkuthuoduvai", "Mankulam", "Mullativu", "Mullivaikkal", "Mulliyawalai", "Muthauyan Kaddakulam", "Naddan Kandal", "Odduchudan", "Puthuvedduvan", "Thunukkai", "Udayarkaddu", "Vavunakkulam", "Visvamadukulam", "Yogapuram", "Agarapathana", "Ambagamuwa Udabulathgama", "Ambatalawa", "Ambewela", "Bogawantalawa", "Bopattalawa", "Dagampitiya", "Dayagama Bazaar", "Dikoya", "Doragala", "Dunukedeniya", "Ginigathena", "Gonakele", "Haggala", "Halgranoya", "Hangarapitiya", "Hapugastalawa", "Harangalagama", "Harasbedda", "Hatton", "Hedunuwewa", "Hitigegama", "Kalaganwatta", "Kandapola", "Katukitula", "Keerthi Bandarapura", "Kelanigama", "Ketaboola", "Kotagala", "Kotmale", "Kottellena", "Kumbalgamuwa", "Kumbukwela", "Kurupanawela", "Labukele", "Laxapana", "Lindula", "Madulla", "Maldeniya", "Maskeliya", "Maswela", "Mipanawa", "Mipilimana", "Morahenagama", "Munwatta", "Nanuoya", "Nawathispane", "Nayapana Janapadaya", "Nildandahinna", "Nissanka Uyana", "Norwood", "Nuwara Eliya", "Padiyapelella", "Patana", "Pitawala", "Pundaluoya", "Ramboda", "Rozella", "Rupaha", "Ruwaneliya", "Santhipura", "Talawakele", "Teripeha", "Udamadura", "Udapussallawa", "Walapane", "Watagoda", "Watawala", "Widulipura", "Wijebahukanda", "Alutwewa", "Aralaganwila", "Aselapura", "Attanakadawala", "Bakamuna", "Dalukana", "Damminna", "Dewagala", "Dimbulagala", "Divulankadawala", "Divuldamana", "Diyabeduma", "Diyasenpura", "Elahera", "Ellewewa", "Galamuna", "Galoya Junction", "Giritale", "Hansayapalama", "Hingurakdamana", "Hingurakgoda", "Jayanthipura", "Jayasiripura", "Kalingaela", "Kalukele Badanagala", "Kashyapapura", "Kawudulla", "Kawuduluwewa Stagell", "Kottapitiya", "Kumaragama", "Lakshauyana", "Maduruoya", "Maha Ambagaswewa", "Mahatalakolawewa", "Mahawela Sinhapura", "Mampitiya", "Medirigiriya", "Meegaswewa", "Minneriya", "Mutugala", "Nawasenapura", "Nelumwewa", "Nuwaragala", "Onegama", "Orubendi Siyambalawa", "Palugasdamana", "Parakramasamudraya", "Pelatiyawa", "Pimburattewa", "Polonnaruwa", "Pulastigama", "Sevanapitiya", "Sinhagama", "Sungavila", "Talpotha", "Tamankaduwa", "Tambala", "Unagalavehera", "Welikanda", "Wijayabapura", "Yodaela", "Yudaganawa", "Adippala", "Ambakandawila", "Anamaduwa", "Andigama", "Angunawila", "Attawilluwa", "Bangadeniya", "Baranankattuwa", "Battuluoya", "Bujjampola", "Chilaw", "Dankotuwa", "Dunkannawa", "Eluwankulama", "Ettale", "Galmuruwa", "Ihala Kottaramulla", "Ihala Puliyankulama", "Ilippadeniya", "Inginimitiya", "Ismailpuram", "Kakkapalliya", "Kalladiya", "Kalpitiya", "Kandakuliya", "Karativponparappi", "Karawitagara", "Karuwalagaswewa", "Katuneriya", "Kirimundalama", "Koswatta", "Kottantivu", "Kottukachchiya", "Kudawewa", "Kumarakattuwa", "Kurinjanpitiya", "Kuruketiyawa", "Lihiriyagama", "Lunuwila", "Madampe", "Madurankuliya", "Mahakumbukkadawala", "Mahauswewa", "Mahawewa", "Mampuri", "Mangalaeliya", "Marawila", "Mudalakkuliya", "Mugunuwatawana", "Mukkutoduwawa", "Mundel", "Muttibendiwila", "Nainamadama", "Nalladarankattuwa", "Nattandiya", "Nawagattegama", "Norachcholai", "Palaviya", "Pallama", "Palliwasalturai", "Panirendawa", "Pothuwatawana", "Puttalam", "Puttalam Cement Factory", "Rajakadaluwa", "Saliyawewa Junction", "Serukele", "Sirambiadiya", "Siyambalagashene", "Tabbowa", "Talawila Church", "Toduwawa", "Udappuwa", "Uridyawa", "Vanathawilluwa", "Waikkal", "Watugahamulla", "Wennappuwa", "Wijeyakatupotha", "Wilpotha", "Yogiyana", "Akarella", "Atakalanpanna", "Ayagama", "Balangoda", "Batatota", "Belihuloya", "Bolthumbe", "Bomluwageaina", "Bulutota", "Dambuluwana", "Daugala", "Dela", "Delwala", "Demuwatha", "Dodampe", "Doloswalakanda", "Dumbara Manana", "Eheliyagoda", "Elapatha", "Ellagawa", "Ellaulla", "Ellawala", "Embilipitiya", "Eratna", "Erepola", "Gabbela", "Gallella", "Gangeyaya", "Gawaragiriya", "Getahetta", "Gillimale", "Godagampola", "Godakawela", "Gurubewilagama", "Halpe", "Halwinna", "Handagiriya", "Hapugastenna", "Hatangala", "Hatarabage", "Hidellana", "Hiramadagama", "Ihalagama", "Ittakanda", "Kahangama", "Kahawatta", "Kalawana", "Kaltota", "Karandana", "Karangoda", "Kella Junction", "Kiriella", "Kolambageara", "Kolombugama", "Kolonna", "Kudawa", "Kuruwita", "Lellopitiya", "lmbulpe", "Madalagama", "Mahawalatenna", "Makandura Sabara", "Malwala Junction", "Marapana", "Matuwagalagama", "Medagalatur", "Meddekanda", "Minipura Dumbara", "Mitipola", "Morahela", "Mulendiyawala", "Mulgama", "Nawalakanda", "NawinnaPinnakanda", "Niralagama", "Nivitigala", "Omalpe", "Opanayaka", "Padalangala", "Pallebedda", "Pambagolla", "Panamura", "Panapitiya", "Panapola", "Panawala", "Parakaduwa", "Pebotuwa", "Pelmadulla", "Pimbura", "Pinnawala", "Pothupitiya", "Rajawaka", "Rakwana", "Ranwala", "Rassagala", "Ratna Hangamuwa", "Ratnapura", "Samanalawewa", "Sri Palabaddala", "Sudagala", "Talakolahinna", "Tanjantenna", "Teppanawa", "Tunkama", "Udaha Hawupe", "Udakarawita", "Udaniriella", "Udawalawe", "Ullinduwawa", "Veddagala", "Vijeriya", "Waleboda", "Watapotha", "Waturawa", "Weligepola", "Welipathayaya", "Wewelwatta", "Wikiliya", "Agbopura", "Buckmigama", "Chinabay", "Dehiwatte", "Echchilampattai", "Galmetiyawa", "Gomarankadawala", "Kaddaiparichchan", "Kanniya", "Kantalai", "Kantalai Sugar Factory", "Kiliveddy", "Kinniya", "Kuchchaveli", "Kumburupiddy", "Kurinchakemy", "Lankapatuna", "Mahadivulwewa", "Maharugiramam", "Mallikativu", "Mawadichenai", "Mullipothana", "Mutur", "Neelapola", "Nilaveli", "Pankulam", "Rottawewa", "Sampaltivu", "Sampur", "Serunuwara", "Seruwila", "Sirajnagar", "Somapura", "Tampalakamam", "Tiriyayi", "Toppur", "Trincomalee", "Vellamanal", "Wanela", "Akkarayankulam", "Cheddikulam", "Chemamadukkulam", "Elephant Pass", "Eluthumadduval", "Iranai lluppaikulam", "Iranaitiv", "Kanagarayankulam", "Kanavil", "Kandavalai", "Kavutharimunai", "Kilinochchi", "lyakachchi", "Mahakachchakodiya", "Mamaduwa", "Maraiyadithakulam", "Mulliyan", "Murasumoddai", "Nedunkemy", "Neriyakulam", "Omanthai", "Palamoddai", "Pallai", "Pallavarayankaddu", "Pampaimadu", "Paranthan", "Pavaikulam", "Periyathambanai", "Periyaulukkulam", "Purakari Nallur", "Ramanathapuram", "Sasthrikulankulam", "Sivapuram", "Skanthapuram", "Thalayadi", "Tharmapuram", "Uruthirapuram", "Vaddakachchi", "Vannerikkulam", "Varikkuttiyoor", "Vavuniya", "Veravil", "Vinayagapuram"};

    public static boolean isEmailValid(String email) {

        String regExpn = "^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
                + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$";

        CharSequence inputStr = email;

        pattern = Pattern.compile(regExpn, Pattern.CASE_INSENSITIVE);
        matcher = pattern.matcher(inputStr);

        if (matcher.matches())
            return true;
        else
            return false;
    }

    public static String randomString(int len){
        StringBuilder sb = new StringBuilder(len);
        for( int i = 0; i < len; i++ )
            sb.append( AB.charAt( rnd.nextInt(AB.length()) ) );
        return sb.toString();
    }

    public static String getDeviceID(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
        return telephonyManager.getDeviceId();
    }

    //HTTP RESPONSE CODES
    public static final int OK = 200;
    public static final int BAD_REQUEST = 400;
    public static final int REQUEST_TIMEOUT = 408;
    public static final int INTERNAL_SERVER_ERROR = 500;
    public static final int BAD_GATEWAY = 502;

    public static String getErrorResponseMessage(int code){

        String error;

        switch (code) {
            case BAD_REQUEST:  error =   "Bad request occurred, Please try again later";
                break;
            case REQUEST_TIMEOUT:  error =  "Request timed out, Please try again later";
                break;
            case INTERNAL_SERVER_ERROR:  error =  "Server error occurred, Please try again later";
                break;
            case BAD_GATEWAY:  error =  "Bad gateway found, Please try again later";
                break;
            default: error = "Network error occurred, Please try again later";
                break;
        }

        return error;

    }

    public static void setDeviceName(Context c, String deviceName){
        SharedPreferences pref = c.getSharedPreferences(PREF_NAME,
                MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(KEY_DEVICE_NAME, deviceName) ;
        editor.commit() ;
    }

    public static String getDeviceName(Context c){
        SharedPreferences pref = c.getSharedPreferences(PREF_NAME,
                MODE_PRIVATE);
        return pref.getString(KEY_DEVICE_NAME, "Mydevice") ;
    }

    public static boolean signOutUser(Context c){
        SharedPreferences pref =  c.getSharedPreferences("loginPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("syncId",null);
        editor.commit();
        return true;
    }


}
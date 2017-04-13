package net.sky.scweather.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import net.sky.scweather.data.WeatherContract.*;

class WeatherDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 4;
    static final String DATABASE_NAME = "scweather.db";

    WeatherDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_CITY_TABLE = String.format("CREATE TABLE %s (%s INTEGER PRIMARY KEY, %s TEXT UNIQUE NOT NULL);",
                CityEntry.TABLE_NAME, CityEntry._ID, CityEntry.COLUMN_CITY_NAME);
        sqLiteDatabase.execSQL(SQL_CREATE_CITY_TABLE);

        final String SQL_POPULATE_CITY_TABLE = String.format("INSERT INTO %s (%s,%s) values " +
                        "(4200051,'Abdon Batista')," +
                        "(4200101,'Abelardo Luz')," +
                        "(4200200,'Agrolândia')," +
                        "(4200309,'Agronômica')," +
                        "(4200408,'Água Doce')," +
                        "(4200507,'Águas de Chapecó')," +
                        "(4200556,'Águas Frias')," +
                        "(4200606,'Águas Mornas')," +
                        "(4200705,'Alfredo Wagner')," +
                        "(4200754,'Alto Bela Vista')," +
                        "(4200804,'Anchieta')," +
                        "(4200903,'Angelina')," +
                        "(4201000,'Anita Garibaldi')," +
                        "(4201109,'Anitápolis')," +
                        "(4201208,'Antônio Carlos')," +
                        "(4201257,'Apiúna')," +
                        "(4201273,'Arabutã')," +
                        "(4201307,'Araquari')," +
                        "(4201406,'Araranguá')," +
                        "(4201505,'Armazém')," +
                        "(4201604,'Arroio Trinta')," +
                        "(4201653,'Arvoredo')," +
                        "(4201703,'Ascurra')," +
                        "(4201802,'Atalanta')," +
                        "(4201901,'Aurora')," +
                        "(4201950,'Balneário Arroio do Silva')," +
                        "(4202008,'Balneário Camboriú')," +
                        "(4202057,'Balneário Barra do Sul')," +
                        "(4202073,'Balneário Gaivota')," +
                        "(4202081,'Bandeirante')," +
                        "(4202099,'Barra Bonita')," +
                        "(4202107,'Barra Velha')," +
                        "(4202131,'Bela Vista do Toldo')," +
                        "(4202156,'Belmonte')," +
                        "(4202206,'Benedito Novo')," +
                        "(4202305,'Biguaçu')," +
                        "(4202404,'Blumenau')," +
                        "(4202438,'Bocaina do Sul')," +
                        "(4202453,'Bombinhas')," +
                        "(4202503,'Bom Jardim da Serra')," +
                        "(4202537,'Bom Jesus')," +
                        "(4202578,'Bom Jesus do Oeste')," +
                        "(4202602,'Bom Retiro')," +
                        "(4202701,'Botuverá')," +
                        "(4202800,'Braço do Norte')," +
                        "(4202859,'Braço do Trombudo')," +
                        "(4202875,'Brunópolis')," +
                        "(4202909,'Brusque')," +
                        "(4203006,'Caçador')," +
                        "(4203105,'Caibi')," +
                        "(4203154,'Calmon')," +
                        "(4203204,'Camboriú')," +
                        "(4203253,'Capão Alto')," +
                        "(4203303,'Campo Alegre')," +
                        "(4203402,'Campo Belo do Sul')," +
                        "(4203501,'Campo Erê')," +
                        "(4203600,'Campos Novos')," +
                        "(4203709,'Canelinha')," +
                        "(4203808,'Canoinhas')," +
                        "(4203907,'Capinzal')," +
                        "(4203956,'Capivari de Baixo')," +
                        "(4204004,'Catanduvas')," +
                        "(4204103,'Caxambu do Sul')," +
                        "(4204152,'Celso Ramos')," +
                        "(4204178,'Cerro Negro')," +
                        "(4204194,'Chapadão do Lageado')," +
                        "(4204202,'Chapecó')," +
                        "(4204251,'Cocal do Sul')," +
                        "(4204301,'Concórdia')," +
                        "(4204350,'Cordilheira Alta')," +
                        "(4204400,'Coronel Freitas')," +
                        "(4204459,'Coronel Martins')," +
                        "(4204509,'Corupá')," +
                        "(4204558,'Correia Pinto')," +
                        "(4204608,'Criciúma')," +
                        "(4204707,'Cunha Porã')," +
                        "(4204756,'Cunhataí')," +
                        "(4204806,'Curitibanos')," +
                        "(4204905,'Descanso')," +
                        "(4205001,'Dionísio Cerqueira')," +
                        "(4205100,'Dona Emma')," +
                        "(4205159,'Doutor Pedrinho')," +
                        "(4205175,'Entre Rios')," +
                        "(4205191,'Ermo')," +
                        "(4205209,'Erval Velho')," +
                        "(4205308,'Faxinal dos Guedes')," +
                        "(4205357,'Flor do Sertão')," +
                        "(4205407,'Florianópolis')," +
                        "(4205431,'Formosa do Sul')," +
                        "(4205456,'Forquilhinha')," +
                        "(4205506,'Fraiburgo')," +
                        "(4205555,'Frei Rogério')," +
                        "(4205605,'Galvão')," +
                        "(4205704,'Garopaba')," +
                        "(4205803,'Garuva')," +
                        "(4205902,'Gaspar')," +
                        "(4206009,'Governador Celso Ramos')," +
                        "(4206108,'Grão Pará')," +
                        "(4206207,'Gravatal')," +
                        "(4206306,'Guabiruba')," +
                        "(4206405,'Guaraciaba')," +
                        "(4206504,'Guaramirim')," +
                        "(4206603,'Guarujá do Sul')," +
                        "(4206652,'Guatambú')," +
                        "(4206702,'Herval d\'\'Oeste')," +
                        "(4206751,'Ibiam')," +
                        "(4206801,'Ibicaré')," +
                        "(4206900,'Ibirama')," +
                        "(4207007,'Içara')," +
                        "(4207106,'Ilhota')," +
                        "(4207205,'Imaruí')," +
                        "(4207304,'Imbituba')," +
                        "(4207403,'Imbuia')," +
                        "(4207502,'Indaial')," +
                        "(4207577,'Iomerê')," +
                        "(4207601,'Ipira')," +
                        "(4207650,'Iporã do Oeste')," +
                        "(4207684,'Ipuaçu')," +
                        "(4207700,'Ipumirim')," +
                        "(4207759,'Iraceminha')," +
                        "(4207809,'Irani')," +
                        "(4207858,'Irati')," +
                        "(4207908,'Irineópolis')," +
                        "(4208005,'Itá')," +
                        "(4208104,'Itaiópolis')," +
                        "(4208203,'Itajaí')," +
                        "(4208302,'Itapema')," +
                        "(4208401,'Itapiranga')," +
                        "(4208450,'Itapoá')," +
                        "(4208500,'Ituporanga')," +
                        "(4208609,'Jaborá')," +
                        "(4208708,'Jacinto Machado')," +
                        "(4208807,'Jaguaruna')," +
                        "(4208906,'Jaraguá do Sul')," +
                        "(4208955,'Jardinópolis')," +
                        "(4209003,'Joaçaba')," +
                        "(4209102,'Joinville')," +
                        "(4209151,'José Boiteux')," +
                        "(4209177,'Jupiá')," +
                        "(4209201,'Lacerdópolis')," +
                        "(4209300,'Lages')," +
                        "(4209409,'Laguna')," +
                        "(4209458,'Lajeado Grande')," +
                        "(4209508,'Laurentino')," +
                        "(4209607,'Lauro Muller')," +
                        "(4209706,'Lebon Régis')," +
                        "(4209805,'Leoberto Leal')," +
                        "(4209854,'Lindóia do Sul')," +
                        "(4209904,'Lontras')," +
                        "(4210001,'Luiz Alves')," +
                        "(4210035,'Luzerna')," +
                        "(4210050,'Macieira')," +
                        "(4210100,'Mafra')," +
                        "(4210209,'Major Gercino')," +
                        "(4210308,'Major Vieira')," +
                        "(4210407,'Maracajá')," +
                        "(4210506,'Maravilha')," +
                        "(4210555,'Marema')," +
                        "(4210605,'Massaranduba')," +
                        "(4210704,'Matos Costa')," +
                        "(4210803,'Meleiro')," +
                        "(4210852,'Mirim Doce')," +
                        "(4210902,'Modelo')," +
                        "(4211009,'Mondaí')," +
                        "(4211058,'Monte Carlo')," +
                        "(4211108,'Monte Castelo')," +
                        "(4211207,'Morro da Fumaça')," +
                        "(4211256,'Morro Grande')," +
                        "(4211306,'Navegantes')," +
                        "(4211405,'Nova Erechim')," +
                        "(4211454,'Nova Itaberaba')," +
                        "(4211504,'Nova Trento')," +
                        "(4211603,'Nova Veneza')," +
                        "(4211652,'Novo Horizonte')," +
                        "(4211702,'Orleans')," +
                        "(4211751,'Otacílio Costa')," +
                        "(4211801,'Ouro')," +
                        "(4211850,'Ouro Verde')," +
                        "(4211876,'Paial')," +
                        "(4211892,'Painel')," +
                        "(4211900,'Palhoça')," +
                        "(4212007,'Palma Sola')," +
                        "(4212056,'Palmeira')," +
                        "(4212106,'Palmitos')," +
                        "(4212205,'Papanduva')," +
                        "(4212239,'Paraíso')," +
                        "(4212254,'Passo de Torres')," +
                        "(4212270,'Passos Maia')," +
                        "(4212304,'Paulo Lopes')," +
                        "(4212403,'Pedras Grandes')," +
                        "(4212502,'Penha')," +
                        "(4212601,'Peritiba')," +
                        "(4212650,'Pescaria Brava')," +
                        "(4212700,'Petrolândia')," +
                        "(4212809,'Balneário Piçarras')," +
                        "(4212908,'Pinhalzinho')," +
                        "(4213005,'Pinheiro Preto')," +
                        "(4213104,'Piratuba')," +
                        "(4213153,'Planalto Alegre')," +
                        "(4213203,'Pomerode')," +
                        "(4213302,'Ponte Alta')," +
                        "(4213351,'Ponte Alta do Norte')," +
                        "(4213401,'Ponte Serrada')," +
                        "(4213500,'Porto Belo')," +
                        "(4213609,'Porto União')," +
                        "(4213708,'Pouso Redondo')," +
                        "(4213807,'Praia Grande')," +
                        "(4213906,'Presidente Castelo Branco')," +
                        "(4214003,'Presidente Getúlio')," +
                        "(4214102,'Presidente Nereu')," +
                        "(4214151,'Princesa')," +
                        "(4214201,'Quilombo')," +
                        "(4214300,'Rancho Queimado')," +
                        "(4214409,'Rio das Antas')," +
                        "(4214508,'Rio do Campo')," +
                        "(4214607,'Rio do Oeste')," +
                        "(4214706,'Rio dos Cedros')," +
                        "(4214805,'Rio do Sul')," +
                        "(4214904,'Rio Fortuna')," +
                        "(4215000,'Rio Negrinho')," +
                        "(4215059,'Rio Rufino')," +
                        "(4215075,'Riqueza')," +
                        "(4215109,'Rodeio')," +
                        "(4215208,'Romelândia')," +
                        "(4215307,'Salete')," +
                        "(4215356,'Saltinho')," +
                        "(4215406,'Salto Veloso')," +
                        "(4215455,'Sangão')," +
                        "(4215505,'Santa Cecília')," +
                        "(4215554,'Santa Helena')," +
                        "(4215604,'Santa Rosa de Lima')," +
                        "(4215653,'Santa Rosa do Sul')," +
                        "(4215679,'Santa Terezinha')," +
                        "(4215687,'Santa Terezinha do Progresso')," +
                        "(4215695,'Santiago do Sul')," +
                        "(4215703,'Santo Amaro da Imperatriz')," +
                        "(4215752,'São Bernardino')," +
                        "(4215802,'São Bento do Sul')," +
                        "(4215901,'São Bonifácio')," +
                        "(4216008,'São Carlos')," +
                        "(4216057,'São Cristovão do Sul')," +
                        "(4216107,'São Domingos')," +
                        "(4216206,'São Francisco do Sul')," +
                        "(4216255,'São João do Oeste')," +
                        "(4216305,'São João Batista')," +
                        "(4216354,'São João do Itaperiú')," +
                        "(4216404,'São João do Sul')," +
                        "(4216503,'São Joaquim')," +
                        "(4216602,'São José')," +
                        "(4216701,'São José do Cedro')," +
                        "(4216800,'São José do Cerrito')," +
                        "(4216909,'São Lourenço do Oeste')," +
                        "(4217006,'São Ludgero')," +
                        "(4217105,'São Martinho')," +
                        "(4217154,'São Miguel da Boa Vista')," +
                        "(4217204,'São Miguel do Oeste')," +
                        "(4217253,'São Pedro de Alcântara')," +
                        "(4217303,'Saudades')," +
                        "(4217402,'Schroeder')," +
                        "(4217501,'Seara')," +
                        "(4217550,'Serra Alta')," +
                        "(4217600,'Siderópolis')," +
                        "(4217709,'Sombrio')," +
                        "(4217758,'Sul Brasil')," +
                        "(4217808,'Taió')," +
                        "(4217907,'Tangará')," +
                        "(4217956,'Tigrinhos')," +
                        "(4218004,'Tijucas')," +
                        "(4218103,'Timbé do Sul')," +
                        "(4218202,'Timbó')," +
                        "(4218251,'Timbó Grande')," +
                        "(4218301,'Três Barras')," +
                        "(4218350,'Treviso')," +
                        "(4218400,'Treze de Maio')," +
                        "(4218509,'Treze Tílias')," +
                        "(4218608,'Trombudo Central')," +
                        "(4218707,'Tubarão')," +
                        "(4218756,'Tunápolis')," +
                        "(4218806,'Turvo')," +
                        "(4218855,'União do Oeste')," +
                        "(4218905,'Urubici')," +
                        "(4218954,'Urupema')," +
                        "(4219002,'Urussanga')," +
                        "(4219101,'Vargeão')," +
                        "(4219150,'Vargem')," +
                        "(4219176,'Vargem Bonita')," +
                        "(4219200,'Vidal Ramos')," +
                        "(4219309,'Videira')," +
                        "(4219358,'Vitor Meireles')," +
                        "(4219408,'Witmarsum')," +
                        "(4219507,'Xanxerê')," +
                        "(4219606,'Xavantina')," +
                        "(4219705,'Xaxim')," +
                        "(4219853,'Zortéa')," +
                        "(4220000,'Balneário Rincão');",
                CityEntry.TABLE_NAME, CityEntry._ID, CityEntry.COLUMN_CITY_NAME);
        sqLiteDatabase.execSQL(SQL_POPULATE_CITY_TABLE);

        final String SQL_CREATE_SAVED_CITY_TABLE =
                "CREATE TABLE " + SavedCityEntry.TABLE_NAME + " (" +
                        SavedCityEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        SavedCityEntry.COLUMN_CITY_FK + " INTEGER NOT NULL, " +
                        " FOREIGN KEY (" + SavedCityEntry.COLUMN_CITY_FK + ") REFERENCES " +
                        CityEntry.TABLE_NAME + " (" + CityEntry._ID + "), " +
                        " UNIQUE (" + SavedCityEntry.COLUMN_CITY_FK + ") ON CONFLICT REPLACE);";
        sqLiteDatabase.execSQL(SQL_CREATE_SAVED_CITY_TABLE);

        final String SQL_POPULATE_SAVED_CITY_TABLE = "INSERT INTO " + SavedCityEntry.TABLE_NAME +
                " (" + SavedCityEntry.COLUMN_CITY_FK + ") values " + /* Florianópolis */ "(4205407)," +
                /* Joinville */ "(4209102);";
        sqLiteDatabase.execSQL(SQL_POPULATE_SAVED_CITY_TABLE);

        final String SQL_CREATE_WEATHER_TABLE =
                "CREATE TABLE " + WeatherEntry.TABLE_NAME + " (" +
                        WeatherEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        WeatherEntry.COLUMN_CITY_FK + " INTEGER NOT NULL, " +
                        WeatherEntry.COLUMN_FORECAST_DATE + " INTEGER NOT NULL, " +
                        WeatherEntry.COLUMN_LAST_UPDATE + " INTEGER NOT NULL, " +
                        WeatherEntry.COLUMN_ICON + " INTEGER NOT NULL, " +
                        WeatherEntry.COLUMN_DESCRIPTION + " TEXT NOT NULL, " +
                        WeatherEntry.COLUMN_DAY_PERIOD + " INTEGER NOT NULL, " +
                        WeatherEntry.COLUMN_RAIN + " INTEGER NOT NULL, " +
                        WeatherEntry.COLUMN_RELATIVE_HUMIDITY + " INTEGER NOT NULL, " +
                        WeatherEntry.COLUMN_WIND_DIRECTION_START + " TEXT NOT NULL, " +
                        WeatherEntry.COLUMN_WIND_DIRECTION_END + " TEXT NOT NULL, " +
                        WeatherEntry.COLUMN_WIND_SPEED_MAX + " INTEGER NOT NULL, " +
                        WeatherEntry.COLUMN_WIND_SPEED_AVG + " INTEGER NOT NULL, " +
                        WeatherEntry.COLUMN_TEMPERATURE_MAX + " INTEGER NOT NULL, " +
                        WeatherEntry.COLUMN_TEMPERATURE_MIN + " INTEGER NOT NULL, " +
                        // set city_fk reference to city._id
                        " FOREIGN KEY (" + WeatherEntry.COLUMN_CITY_FK + ") REFERENCES " +
                        CityEntry.TABLE_NAME + " (" + CityEntry._ID + "), " +
                        // unique (city, date, period) tuple
                        " UNIQUE (" +
                        WeatherEntry.COLUMN_CITY_FK + ", " +
                        WeatherEntry.COLUMN_FORECAST_DATE + ", " +
                        WeatherEntry.COLUMN_DAY_PERIOD +
                        ") ON CONFLICT REPLACE);";
        sqLiteDatabase.execSQL(SQL_CREATE_WEATHER_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int j) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + WeatherEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + SavedCityEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + CityEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}

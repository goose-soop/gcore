package com.guillaumevdn.gcore;

import java.io.File;

import com.guillaumevdn.gcore.lib.messenger.Text;
import com.guillaumevdn.gcore.lib.util.Utils;

public class GLocale {

	// ----------------------------------------------------------------------
	// Fields
	// ----------------------------------------------------------------------

	public static final File file = new File(GCore.inst().getDataFolder() + "/texts.yml");
	public static final File fileUnknown = new File(GCore.inst().getDataFolder() + "/texts_unknown.yml");

	// ----------------------------------------------------------------------
	// Messages
	// ----------------------------------------------------------------------

	// generic
	public static final Text MSG_GENERIC_NOPERMISSION = new Text(
			"MSG_GENERIC_NOPERMISSION", file,
			"en_US", "&6{plugin} >> &7You don't have the permission to do this.",
			"es_ES", Utils.asList("&6{plugin} >> &7No posees permiso para realizar esto."),
			"fr_FR", "&6{plugin} >> &7Vous n'avez pas la permission de faire cela.",
			"hu_HU", "&6{plugin} >> &7Nincs jogod ehhez.",
			"it_IT", Utils.asList("&6{plugin} >> &7Non hai il permesso di farlo."),
			"zh_TW", Utils.asList("&6{plugin} >> &7你沒有權限這麼做")
			);

	public static final Text MSG_GENERIC_NOHANDITEM = new Text(
			"MSG_GENERIC_NOHANDITEM", file,
			"en_US", "&6{plugin} >> &7You have to hold an item in your hand.",
			"fr_FR", "&6{plugin} >> &7Vous devez tenir un item dans votre main.",
			"hu_HU", "&6{plugin} >> &7Tartanod kell egy elemet a kezedben.",
			"it_IT", Utils.asList("&6{plugin} >> &7Devi tenere un oggetto in mano."),
			"es_ES", Utils.asList("&6{plugin} >> &7Debes poseer un item en la mano"),
			"zh_TW", Utils.asList("&6{plugin} >> &7你必須要把物品拿在手上")
			);

	public static final Text MSG_GENERIC_NOMONEY = new Text(
			"MSG_GENERIC_NOMONEY", file,
			"en_US", "&6{plugin} >> &7You need &c{money}$ &7but you only have &c{balance}$ &7in your bank account.",
			"fr_FR", "&6{plugin} >> &7Vous avez besoin de &c{money}$ &7mais vous avez seulement &c{balance}$ &7dans votre compte en banque.",
			"hu_HU", "&6{plugin} >> &7Neked kell &c{money}$ &7de csak van &c{balance}$ &7a bank fiókodban.",
			"it_IT", Utils.asList("&6{plugin} >> &7Hai bisogno di &c{money}$ &7ma hai solo &c{balance}$ &7a nel tuo account."),
			"es_ES", Utils.asList("&6{plugin} >> &7Requieres &c{money}$ pero solo posees &c{balance}$ en tu"),
			"zh_TW", Utils.asList("&6{plugin} >> &7你需要 &c{money}$ &7但是你身上目前只剩 &c{balance}$")
			);

	public static final Text MSG_GENERIC_NOXPLEVEL = new Text(
			"MSG_GENERIC_NOXPLEVEL", file,
			"en_US", "&6{plugin} >> &7You need &c{amount} LVL &7but you only have &c{balance}&7.",
			"fr_FR", "&6{plugin} >> &7Vous avez besoin de &c{amount} LVL &7mais vous avez seulement &c{balance}&7.",
			"zh_TW", Utils.asList("&6{plugin} >> &7你需要 &c{amount} LVL &7但是你只有 &c{balance}$")
			);

	public static final Text MSG_GENERIC_NAMETAKEN = new Text(
			"MSG_GENERIC_NAMETAKEN", file,
			"en_US", "&6{plugin} >> &7Name &c{name} &7is already taken.",
			"fr_FR", "&6{plugin} >> &7Le nom &c{name} &7est déjà utilisé.",
			"hu_HU", "&6{plugin} >> &7Név &c{name} &7már foglalt.",
			"it_IT", Utils.asList("&6{plugin} >> &7Il nome &c{name} &7è già in uso."),
			"es_ES", Utils.asList("&6{plugin} >> &7El nombre &c{name} &7 ya está en uso"),
			"zh_TW", Utils.asList("&6{plugin} >> &7名稱 &c{name} &7已經被使用了")
			);

	public static final Text MSG_GENERIC_IDTAKEN = new Text(
			"MSG_GENERIC_IDTAKEN", file,
			"en_US", "&6{plugin} >> &7Id &c{id} &7is already taken.",
			"fr_FR", "&6{plugin} >> &7L'id &c{id} &7est déjà utilisé.",
			"hu_HU", "&6{plugin} >> &7Id &c{id} &7már foglalt.",
			"it_IT", Utils.asList("&6{plugin} >> &7L'Id &c{id} &7è già in uso."),
			"es_ES", Utils.asList("&6{plugin} >> &7La ID &c{id} ya está en uso"),
			"zh_TW", Utils.asList("&6{plugin} >> &7ID &c{id} &7已被使用了")
			);

	public static final Text MSG_GENERIC_INVALIDPLAYER = new Text(
			"MSG_GENERIC_INVALIDPLAYER", file,
			"en_US", "&6{plugin} >> &7Couldn't find online player &c{error}&7.",
			"fr_FR", "&6{plugin} >> &7Impossible de trouver le joueur connecté &c{error}&7.",
			"hu_HU", "&6{plugin} >> &7A játékos &c{player} &7nem található.",
			"it_IT", Utils.asList("&6{plugin} >> &7Non è possibile trovare questo giocatore online &c{error}."),
			"es_ES", Utils.asList("&6{plugin} >> &7No se encontró al jugador conectado &c{error}&7."),
			"zh_TW", Utils.asList("&6{plugin} >> &7找不到線上玩家 &c{error}")
			);

	public static final Text MSG_GENERIC_INVALIDPLAYEROFFLINE = new Text(
			"MSG_GENERIC_INVALIDPLAYEROFFLINE", file,
			"en_US", "&6{plugin} >> &7Player &c{error} &7is invalid.",
			"fr_FR", "&6{plugin} >> &7Le joueur &c{error} &7est invalide.",
			"hu_HU", "&6{plugin} >> &7A játékos &c{error} &7érvénytelen.",
			"it_IT", Utils.asList("&6{plugin} >> &7Il giocatore &c{error} &7non è valido."),
			"es_ES", Utils.asList("&6{plugin} >> &7El jugador &c{error} &7no es válido"),
			"zh_TW", Utils.asList("&6{plugin} >> &7玩家 &c{error} &7不存在")
			);

	public static final Text MSG_GENERIC_INVALIDDOUBLE = new Text(
			"MSG_GENERIC_INVALIDDOUBLE", file,
			"en_US", "&6{plugin} >> &7Decimal number &c{error} &7is invalid.",
			"fr_FR", "&6{plugin} >> &7Le nombre décimal &c{error} &7est invalide.",
			"hu_HU", "&6{plugin} >> &7Decimális szám &c{error} &7érvénytelen.",
			"it_IT", Utils.asList("&6{plugin} >> &7I numeri decimali &c{error} &7non sono validi."),
			"es_ES", Utils.asList("&6{plugin} >> &7El número decimal &c{error} &7es inválido."),
			"zh_TW", Utils.asList("&6{plugin} >> &7無效的十位數字 &c{error}")
			);

	public static final Text MSG_GENERIC_INVALIDINT = new Text(
			"MSG_GENERIC_INVALIDINT", file,
			"en_US", "&6{plugin} >> &7Number &c{error} &7is invalid.",
			"fr_FR", "&6{plugin} >> &7Le nombre &c{error} &7est invalide.",
			"hu_HU", "&6{plugin} >> &7Szám &c{error} &7érvénytelen.",
			"it_IT", Utils.asList("&6{plugin} >> &7Il numero &c{error} &7non è valido."),
			"es_ES", Utils.asList("&6{plugin} >> &7El número &c{error} &7no es inválido"),
			"zh_TW", Utils.asList("&6{plugin} >> &7無效的數字 &c{error}")
			);

	public static final Text MSG_GENERIC_INVALIDALPHANUMERIC = new Text(
			"MSG_GENERIC_INVALIDALPHANUMERIC", file,
			"en_US", "&6{plugin} >> &7Value &c{error} &7isn't alphanumeric.",
			"fr_FR", "&6{plugin} >> &7La valeur &c{error} &7n'est pas alphanumérique.",
			"hu_HU", "&6{plugin} >> &7Érték &c{error} &7nem alfanumerikus.",
			"it_IT", Utils.asList("&6{plugin} >> &7Il valore &c{error} &7non è alfanumerico."),
			"es_ES", Utils.asList("&6{plugin} >> &7El valor &c{error} &7 no es alfanumérico."),
			"zh_TW", Utils.asList("&6{plugin} >> &7數值 &c{error} &7並不是英文字母")
			);

	public static final Text MSG_GENERIC_NOTPLAYER = new Text(
			"MSG_GENERIC_NOTPLAYER", file,
			"en_US", "&6{plugin} >> &7You have to be in-game to do this.",
			"fr_FR", "&6{plugin} >> &7Vous devez être en jeu pour faire cela.",
			"hu_HU", "&6{plugin} >> &7Ehhez a játékban kell játszani.",
			"pl_PL", "&6{plugin} >> &7Musisz być w grze, aby to zrobić.",
			"it_IT", Utils.asList("&6{plugin} >> &7Devi essere in gioco per farlo."),
			"es_ES", Utils.asList("&6{plugin} >> &7Debes estar en el juego para realizar esto"),
			"zh_TW", Utils.asList("&6{plugin} >> &7你只能在遊戲內執行這項動作")
			);

	public static final Text MSG_GENERIC_INVALIDCROSSHAIRBLOCK = new Text(
			"MSG_GENERIC_INVALIDCROSSHAIRBLOCK", file,
			"en_US", "&6{plugin} >> &7You're not pointing any block.",
			"fr_FR", "&6{plugin} >> &7Vous ne pointez aucun bloc.",
			"hu_HU", "&6{plugin} >> &7Nem nézel egy blokkra sem.",
			"it_IT", Utils.asList("&6{plugin} >> &7Non stai indicando alcun blocco."),
			"es_ES", Utils.asList("&6{plugin} >> &7No estás apuntando a ningún bloque."),
			"zh_TW", Utils.asList("&6{plugin} >> &7你並沒有指向任何方塊")
			);

	public static final Text MSG_GENERIC_INVALIDACTION = new Text(
			"MSG_GENERIC_INVALIDACTION", file,
			"en_US", "&6{plugin} >> &7This action is invalid or has expired.",
			"fr_FR", "&6{plugin} >> &7L'action est invalide ou a expiré.",
			"hu_HU", "&6{plugin} >> &7Ez a művelet érvénytelen vagy lejárt.",
			"it_IT", Utils.asList("&6{plugin} >> &7Questa azione non è valida o è scaduta."),
			"es_ES", Utils.asList("&6{plugin} >> &7Esta acción es invalida o ha expirado"),
			"zh_TW", Utils.asList("&6{plugin} >> &7這是個無效的動作或是已經過期了")
			);

	public static final Text MSG_GENERIC_RELOAD = new Text(
			"MSG_GENERIC_RELOAD", file,
			"en_US", "&6{plugin} >> &7Plugin was reloaded (took {time}).",
			"fr_FR", "&6{plugin} >> &7Le plugin a été reload (took {time}).",
			"hu_HU", "&6{plugin} >> &7A plugin újratöltve (took {time}).",
			"it_IT", Utils.asList("&6{plugin} >> &7Il plugin è stato ricaricato (took {time})."),
			"es_ES", Utils.asList("&6{plugin} >> &7Se ha vuelto a cargar el plugin (tardó {time})."),
			"zh_TW", Utils.asList("&6{plugin} >> &7插件重新讀取成功 (耗時: {time})")
			);

	public static final Text MSG_GENERIC_NOTHING = new Text(
			"MSG_GENERIC_NOTHING", file,
			"en_US", "&6{plugin} >> &7There's nothing to see here.",
			"fr_FR", "&6{plugin} >> &7Il n'y a rien à voir ici.",
			"hu_HU", "&6{plugin} >> &7Itt nincs mit látni.",
			"pl_PL", "&6{plugin} >> &7Tu nic nie ma.",
			"it_IT", Utils.asList("&6{plugin} >> &7Non c'è niente da vedere qui."),
			"es_ES", Utils.asList("&6{plugin} >> &7No hay nada que ver aquí"),
			"zh_TW", Utils.asList("&6{plugin} >> &7這邊沒有任何東西")
			);

	public static final Text MSG_GENERIC_COOLDOWN = new Text(
			"MSG_GENERIC_COOLDOWN", file,
			"en_US", "&6{plugin} >> &7You have to wait &c{time} &7to do this again.",
			"fr_FR", "&6{plugin} >> &7Vous devez attendre &c{time} &7pour faire cela à nouveau.",
			"hu_HU", "&6{plugin} >> &7Meg kell várni &c{time}&7, hogy ezt újra elvégezhesse.",
			"it_IT", Utils.asList("&6{plugin} >> &7Devi aspettare &c{time}&7, per rifarlo."),
			"es_ES", Utils.asList("&6{plugin} >> &7Debes esperar &c{time} &7antes de realizar esto de nuevo"),
			"zh_TW", Utils.asList("&6{plugin} >> &7你必須要等待 &c{time} &7後才能再這麼做")
			);

	public static final Text MSG_GENERIC_COMMAND_DISABLED = new Text(
			"MSG_GENERIC_COMMAND_DISABLED", file,
			"en_US", "&6{plugin} >> &7This command is disabled.",
			"fr_FR", "&6{plugin} >> &7Cette commande est désactivée.",
			"hu_HU", "&6{plugin} >> &7Ez a parancs letiltva.",
			"pl_PL", "&6{plugin} >> &7To polecenie jest wyłączone.",
			"ru_RU", "&6{plugin} >> &7Эта команда отключена.",
			"sv_FI", "&6{plugin} >> &7Tämä komento ei ole käytössä.",
			"it_IT", Utils.asList("&6{plugin} >> &7Questo comando è disabilitato."),
			"es_ES", Utils.asList("&6{plugin} >> &7Comando desactivado."),
			"zh_TW", Utils.asList("&6{plugin} >> &7這個指令已被關閉")
			);

	public static final Text MSG_GENERIC_COMMAND_NOCHILDREN = new Text(
			"MSG_GENERIC_COMMAND_NOCHILDREN", file,
			"en_US", "&6{plugin} >> &7You specified too many arguments, it should end after &c{current_path}&7. Use &c{current_path} -help &7or &c-help:page &7and refer to tab completion to show help.",
			"fr_FR", "&6{plugin} >> &7Vous avez spécifié trop d'arguments, cela devrait s'arrêter après &c{current_path}&7. Utilisez &c{current_path} -help &7ou &c-help:page &7and refer to tab completion to show help.",
			"hu_HU", "&6{plugin} >> &7Túl sok argumentumot adtál meg, a &c{current_path}&7. Use &c{current_path} -help &7or &c-help:page &7and refer to tab completion to show help.",
			"it_IT", Utils.asList("&6{plugin} >> &7Hai inserito troppi argomenti, potrebbe finire dopo &c{current_path}&7."),
			"es_ES", Utils.asList("&6{plugin} >> &7Has especificado demasiadas variables, debería terminar"),
			"zh_TW", Utils.asList("&6{plugin} >> &7你輸入太多的參數了，在 &c{current_path} &7後就不應該輸入其他數值了。 使用 &c{current_path}")
			);

	public static final Text MSG_GENERIC_COMMAND_NOCHILDPERFORMED = new Text(
			"MSG_GENERIC_COMMAND_NOCHILDPERFORMED", file,
			"en_US", "&6{plugin} >> &7Couldn't understand the arguments you specified after &c{current_path}&7.",
			"fr_FR", "&6{plugin} >> &7Impossible de comprendre les arguments que vous avez spécifié après &c{current_path}&7.",
			"hu_HU", "&6{plugin} >> &7Nem sikerült megérteni a &c{current_path}&7 után megadott argumentumokat&7.",
			"it_IT", Utils.asList("&6{plugin} >> &7Non è possibile capire gli argomenti specificati dopo &c{current_path}&7.."),
			"es_ES", Utils.asList("&6{plugin} >> &7No ha sido posible entender los parametros que has especificado"),
			"zh_TW", Utils.asList("&6{plugin} >> &7系統無法讀懂，你輸入的指令 &c{current_path} &7後面的參數")
			);

	public static final Text MSG_GENERIC_COMMAND_MISSINGPARAM = new Text(
			"MSG_GENERIC_COMMAND_MISSINGPARAM", file,
			"en_US", "&6{plugin} >> &7You're missing parameter &c{parameter}&7.",
			"fr_FR", "&6{plugin} >> &7Il manque le paramètre &c{parameter}&7.",
			"hu_HU", "&6{plugin} >> &7A paraméter hiányzik &c{parameter}&7.",
			"it_IT", Utils.asList("&6{plugin} >> &7Hai mancato il parametro &c{parameter}&7."),
			"es_ES", Utils.asList("&6{plugin} >> &7Te ha faltado el parámetro &c{parameter}&7."),
			"zh_TW", Utils.asList("&6{plugin} >> &7你似乎漏掉一些參數了 &c{parameter}")
			);

	public static final Text MSG_GENERIC_COMMAND_INVALIDALPHANUMERICPARAM = new Text(
			"MSG_GENERIC_COMMAND_INVALIDALPHANUMERICPARAM", file,
			"en_US", "&6{plugin} >> &7Parameter &c{parameter} &7should be alphanumeric.",
			"fr_FR", "&6{plugin} >> &7Le paramètre &c{parameter} &7devrait être alphanumérique.",
			"hu_HU", "&6{plugin} >> &7A paraméter &c{parameter} &7alfanumerikusnak kell lennie.",
			"it_IT", Utils.asList("&6{plugin} >> &7Il parametro &c{parameter} &7deve essere alfanumerico."),
			"es_ES", Utils.asList("&6{plugin} >> &7El parámetro &c{parameter} &7debería ser alfanumérico"),
			"zh_TW", Utils.asList("&6{plugin} >> &7參數 &c{parameter} &7字母")
			);

	public static final Text MSG_GENERIC_COMMAND_INVALIDINTPARAM = new Text(
			"MSG_GENERIC_COMMAND_INVALIDINTPARAM", file,
			"en_US", "&6{plugin} >> &7Parameter &c{parameter} &7should be a number.",
			"fr_FR", "&6{plugin} >> &7Le paramètre &c{parameter} &7devrait être un nombre entier.",
			"hu_HU", "&6{plugin} >> &7A paraméter &c{parameter} &7számnak kell lennie.",
			"it_IT", Utils.asList("&6{plugin} >> &7Il parametro &c{parameter} &7dovrebbe essere un numero."),
			"es_ES", Utils.asList("&6{plugin} >> &7El parámetro &c{parameter} &7 debería ser un número'  "),
			"zh_TW", Utils.asList("&6{plugin} >> &7參數 &c{parameter} &7必須要是 數字")
			);

	public static final Text MSG_GENERIC_COMMAND_INVALIDDOUBLEPARAM = new Text(
			"MSG_GENERIC_COMMAND_INVALIDDOUBLEPARAM", file,
			"en_US", "&6{plugin} >> &7Parameter &c{parameter} &7should be a decimal number.",
			"fr_FR", "&6{plugin} >> &7Le paramètre &c{parameter} &7devrait être un nombre décimal.",
			"hu_HU", "&6{plugin} >> &7A paraméter &c{parameter} &7tizedes számnak kell lennie.",
			"it_IT", Utils.asList("&6{plugin} >> &7Il parametro &c{parameter} &7dovrebbe essere un numero decimale."),
			"es_ES", Utils.asList("&6{plugin} >> &7El parámetro &c{parameter} &7 debería ser un número decimal'  "),
			"zh_TW", Utils.asList("&6{plugin} >> &7參數 &c{parameter} &7必須要是 十位數字")
			);

	public static final Text MSG_GENERIC_COMMAND_INVALIDENUMPARAM = new Text(
			"MSG_GENERIC_COMMAND_INVALIDENUMPARAM", file,
			"en_US", "&6{plugin} >> &7Parameter &c{parameter} &7should be a valid &c{enum}&7.",
			"fr_FR", "&6{plugin} >> &7Le paramètre &c{parameter} &7devrait être un &c{enum} &7valide.",
			"hu_HU", "&6{plugin} >> &7A paraméter &c{parameter} &7érvényesnek kell lennie &c{enum}&7.",
			"it_IT", Utils.asList("&6{plugin} >> &7Il parametro &c{parameter} &7deve essere un numero valido &c{enum}&7."),
			"es_ES", Utils.asList("&6{plugin} >> &7El parámetro &c{parameter} &7debería ser un &c{enum}&7 válido."),
			"zh_TW", Utils.asList("&6{plugin} >> &7參數 &c{parameter} &7必須是 &c{enum}")
			);

	public static final Text MSG_GENERIC_COMMAND_INVALIDOFFLINEPLAYERPARAM = new Text(
			"MSG_GENERIC_COMMAND_INVALIDOFFLINEPLAYERPARAM", file,
			"en_US", "&6{plugin} >> &7Parameter &c{parameter} &7should be an existing player.",
			"fr_FR", "&6{plugin} >> &7Le paramètre &c{parameter} &7devrait être un joueur existant.",
			"hu_HU", "&6{plugin} >> &7A paraméter &c{parameter} &7létező játékosnak kell lennie.",
			"it_IT", Utils.asList("&6{plugin} >> &7Il parametro &c{parameter} &7dovrebbe essere un giocatore esistente."),
			"es_ES", Utils.asList("&6{plugin} >> &7El parámetro &c{parameter} &7debería ser un jugador existente"),
			"zh_TW", Utils.asList("&6{plugin} >> &7參數 &c{parameter} &7必須是登入過的玩家")
			);

	public static final Text MSG_GENERIC_COMMAND_INVALIDPLAYERPARAM = new Text(
			"MSG_GENERIC_COMMAND_INVALIDPLAYERPARAM", file,
			"en_US", "&6{plugin} >> &7Parameter &c{parameter} &7should be an online player.",
			"fr_FR", "&6{plugin} >> &7Le paramètre &c{parameter} &7devrait être un joueur connecté.",
			"hu_HU", "&6{plugin} >> &7A paraméter &c{parameter} &7elérhető játékosnak kell lennie.",
			"it_IT", Utils.asList("&6{plugin} >> &7Il parametro &c{parameter} &7dovrebbe essere un giocatore online."),
			"es_ES", Utils.asList("&6{plugin} >>"),
			"zh_TW", Utils.asList("&6{plugin} >> &7參數 &c{parameter} &7必須是線上玩家")
			);

	public static final Text MSG_GENERIC_COMMAND_INVALIDUUIDPARAM = new Text(
			"MSG_GENERIC_COMMAND_INVALIDUUIDPARAM", file,
			"en_US", "&6{plugin} >> &7Parameter &c{parameter} &7should be an UUID.",
			"fr_FR", "&6{plugin} >> &7Le paramètre &c{parameter} &7devrait être un UUID.",
			"hu_HU", "&6{plugin} >> &7A paraméter &c{parameter} &7UUD-nek kell lennie.",
			"it_IT", Utils.asList("&6{plugin} >> &7Il parametro &c{parameter} &7dovrebbe essere un UUID."),
			"es_ES", Utils.asList("&6{plugin} >> &7El parámetro &c{parameter} &7 debería ser un UUID."),
			"zh_TW", Utils.asList("&6{plugin} >> &7參數 &c{parameter} &7必須是 UUID")
			);

	// gcore messages
	public static final Text MSG_GCORE_INVALIDPLUGINPARAM = new Text(
			"MSG_GCORE_INVALIDPLUGINPARAM", file,
			"en_US", "&6{plugin} >> &7Parameter &c{parameter} &7should be a plugin registered with GCore.",
			"fr_FR", "&6{plugin} >> &7Le paramètre &c{parameter} &7devrait être un plugin enregistré avec GCore.",
			"hu_HU", "&6{plugin} >> &7A paraméter &c{parameter} &7a GCore-ban regisztrált bővítménynek kell lennie.",
			"it_IT", Utils.asList("&6{plugin} >> &7Il parametro &c{parameter} &7a dovrebbe essere un plugin registrato con GCore."),
			"es_ES", Utils.asList("&6{plugin} >> &7El parámetro &c{parameter} &7 debería ser un plugin registrado"),
			"zh_TW", Utils.asList("&6{plugin} >> &7參數 &c{parameter} &7必須是和 Gcore 連動的插件")
			);

	public static final Text MSG_GCORE_PLUGINSLIST = new Text(
			"MSG_GCORE_PLUGINSLIST", file,
			"en_US", Utils.asList("&6{plugin} >> &7There are &a{count} plugin{plural} &7registered :", "&a{plugins}"),
			"fr_FR", Utils.asList("&6{plugin} >> &7Il y a &a{count} plugin{plural} &7enregistrés :", "&a{plugins}"),
			"hu_HU", Utils.asList("&6{plugin} >> &7Van &a{count} plugin{plural} &7regisztrálva :", "&a{plugins}"),
			"it_IT", Utils.asList("&6{plugin} >> &7Ci sono &a{count} plugin{plural} &7registrati :", "&a{plugins}"),
			"es_ES", Utils.asList("&6{plugin} >> &7Hay &a{count} plugin{plural} &7registrados :", "&a{plugins}"),
			"zh_TW", Utils.asList("&6Gcore >> &7目前有 &a{count} &7個載入的插件:", "&a{plugins}")
			);

	// ------------------------------------------------------------
	// MISC
	// ------------------------------------------------------------

	// generic
	public static final Text MISC_GENERIC_TIMEFORMATSECONDS = new Text(
			"MISC_GENERIC_TIMEFORMATSECONDS", file,
			"en_US", "{seconds}s",
			"hu_HU", "{seconds}m",
			"it_IT", Utils.asList("{seconds}s"),
			"es_ES", Utils.asList("{seconds}s"),
			"zh_TW", Utils.asList("&f{seconds}&6秒")
			);

	public static final Text MISC_GENERIC_TIMEFORMATMINUTES = new Text(
			"MISC_GENERIC_TIMEFORMATMINUTES", file,
			"en_US", "{minutes}m {seconds}s",
			"hu_HU", "{minutes}p {seconds}m",
			"it_IT", Utils.asList("{minutes}m {seconds}m"),
			"es_ES", Utils.asList("{minutes}m {seconds}s"),
			"zh_TW", Utils.asList("&f{minutes}&6分 &f{seconds}&6秒")
			);

	public static final Text MISC_GENERIC_TIMEFORMATHOURS = new Text(
			"MISC_GENERIC_TIMEFORMATHOURS", file,
			"en_US", "{hours}h {minutes}m {seconds}s",
			"hu_HU", "{hours}ó {minutes}p {seconds}m",
			"it_IT", Utils.asList("{hours}h {minutes}m {seconds}s"),
			"es_ES", Utils.asList("{hours}h {minutes}m {seconds}s"),
			"zh_TW", Utils.asList("&f{hours}&6小時 &f{minutes}&6分 &f{seconds}&6秒")
			);

	public static final Text MISC_GENERIC_TIMEFORMATDAYS = new Text(
			"MISC_GENERIC_TIMEFORMATDAYS", file,
			"en_US", "{days}d {hours}h {minutes}m {seconds}s",
			"hu_HU", "{days}n {hours}ó {minutes}p {seconds}m",
			"it_IT", Utils.asList("{days}d {hours}h {minutes}m {seconds}s"),
			"es_ES", Utils.asList("{days}d {hours}h {minutes}m {seconds}s"),
			"zh_TW", Utils.asList("&f{days}&6天 &f{hours}&6小時 &f{minutes}&6分 &f{seconds}&6秒")
			);

	public static final Text MISC_GENERIC_DAY_MONDAY = new Text(
			"MISC_GENERIC_DAY_MONDAY", file,
			"en_US", "Monday",
			"fr_FR", "lundi",
			"zh_TW", Utils.asList("星期一")
			);

	public static final Text MISC_GENERIC_DAY_TUESDAY = new Text(
			"MISC_GENERIC_DAY_TUESDAY", file,
			"en_US", "Tuesday",
			"fr_FR", "mardi",
			"zh_TW", Utils.asList("星期二")
			);

	public static final Text MISC_GENERIC_DAY_WEDNESDAY = new Text(
			"MISC_GENERIC_DAY_WEDNESDAY", file,
			"en_US", "Wednesday",
			"fr_FR", "mercredi",
			"zh_TW", Utils.asList("星期三")
			);

	public static final Text MISC_GENERIC_DAY_THURSDAY = new Text(
			"MISC_GENERIC_DAY_THURSDAY", file,
			"en_US", "Thursday",
			"fr_FR", "jeudi",
			"zh_TW", Utils.asList("星期四")
			);

	public static final Text MISC_GENERIC_DAY_FRIDAY = new Text(
			"MISC_GENERIC_DAY_FRIDAY", file,
			"en_US", "Friday",
			"fr_FR", "vendredi",
			"zh_TW", Utils.asList("星期五")
			);

	public static final Text MISC_GENERIC_DAY_SATURDAY = new Text(
			"MISC_GENERIC_DAY_SATURDAY", file,
			"en_US", "Saturday",
			"fr_FR", "samedi",
			"zh_TW", Utils.asList("星期六")
			);

	public static final Text MISC_GENERIC_DAY_SUNDAY = new Text(
			"MISC_GENERIC_DAY_SUNDAY", file,
			"en_US", "Sunday",
			"fr_FR", "dimanche",
			"zh_TW", Utils.asList("星期日")
			);

	// ------------------------------------------------------------
	// GUI
	// ------------------------------------------------------------

	// generic
	public static final Text GUI_GENERIC_PREVIOUSPAGEITEM = new Text(
			"GUI_GENERIC_PREVIOUSPAGEITEM", file,
			"en_US", "&6Previous page",
			"fr_FR", "&6Page précédente",
			"hu_HU", "&6Előző oldal",
			"it_IT", Utils.asList("&6Pagina precedente"),
			"es_ES", Utils.asList("&7Página previa"),
			"zh_TW", Utils.asList("&6上一頁")
			);

	public static final Text GUI_GENERIC_NEXTPAGEITEM = new Text(
			"GUI_GENERIC_NEXTPAGEITEM", file,
			"en_US", "&6Next page",
			"fr_FR", "&6Page suivante",
			"hu_HU", "&6Következő oldal",
			"it_IT", Utils.asList("&6Pagina successiva"),
			"es_ES", Utils.asList("&6Página siguiente"),
			"zh_TW", Utils.asList("&6下一頁")
			);

	public static final Text GUI_GENERIC_CONFIRMNAME = new Text(
			"GUI_GENERIC_CONFIRMNAME", file,
			"en_US", "Confirm action",
			"fr_FR", "Confirmer action",
			"hu_HU", "Megerősít akció",
			"it_IT", Utils.asList("Conferma azione"),
			"es_ES", Utils.asList("Confirmar acción"),
			"zh_TW", Utils.asList("確認動作")
			);

	// ------------------------------------------------------------
	// Editor
	// ------------------------------------------------------------

	// editor : messages
	public static final Text MSG_GENERIC_DUPLICATEELEMENT = new Text(
			"MSG_GENERIC_DUPLICATEELEMENT", file,
			"en_US", "&7An element with id &6{id} &7already exists.",
			"fr_FR", "&7Un élément avec l'id &6{id} &7existe déjà.",
			"it_IT", Utils.asList("&7Un elemento con l'id &6{id} &7esiste già."),
			"es_ES", Utils.asList("&7Un elemento con la id &6{id} &7ya existe"),
			"zh_TW", Utils.asList("&7ID &6{id} &7材質已存在")
			);

	public static final Text MSG_GENERIC_CHATINPUT = new Text(
			"MSG_GENERIC_CHATINPUT", file,
			"en_US", "&7Type the new value or &ccancel &7to cancel.",
			"fr_FR", "&7Entrez une nouvelle valeur ou bien &ccancel &7pour annuler.",
			"it_IT", Utils.asList("&7Digita un nuovo valore o &ccancel &7per annullare."),
			"es_ES", Utils.asList("&7Escriba un nuevo valor o &ccancel &7para cancelar."),
			"zh_TW", Utils.asList("&7輸入一個新的數值，或是輸入 &ccancel &7取消")
			);

	public static final Text MSG_GENERIC_CHATINPUTMODIFY = new Text(
			"MSG_GENERIC_CHATINPUTMODIFY", file,
			"en_US", "&7Type the new value or &ccancel &7to cancel. Click on this message to add the existing text.",
			"fr_FR", "&7Entrez une nouvelle valeur ou bien &ccancel &7pour annuler. Cliquez sur ce message pour ajouter le texte existant.",
			"zh_TW", Utils.asList("&7輸入一個新的數值，或是輸入 &ccancel &7取消", "&7(點擊這個訊息將原本的文字貼在輸入區)")
			);

	public static final Text MSG_GENERIC_CHATINPUTID = new Text(
			"MSG_GENERIC_CHATINPUTID", file,
			"en_US", "&7Enter an &aID &7in the chat or &ccancel &7to cancel.",
			"fr_FR", "&7Entrez un &aID &7dans le chat ou &ccancel &7pour annuler.",
			"it_IT", Utils.asList("&7Inserisci un &aID &7in chat o &ccancel &7per annullare."),
			"es_ES", Utils.asList("&7Inserte la &aID &7en el chat o &ccancel para cancelar"),
			"zh_TW", Utils.asList("&7輸入一個 &aID &7，或是輸入 &ccancel &7取消")
			);

	public static final Text MSG_GENERIC_LOCATIONINPUT = new Text(
			"MSG_GENERIC_LOCATIONINPUT", file,
			"en_US", "&7Press 'sneak' when you're ready to import your location, or right-click a block.",
			"fr_FR", "&7Appuyez sur 's'accroupir' lorsque vous serez prêt à importer votre location, ou bien cliquez sur un bloc.",
			"it_IT", Utils.asList("&7Premere 'sneak' quando sei pronto a importare la tua posizione, o tasto destro su un blocco."),
			"es_ES", Utils.asList("&7Presione 'sneak' cuando esté listo para importar su localización, o presione"),
			"zh_TW", Utils.asList("&7站到你要儲存的做標點按下&fSHIFT&7或是&f右鍵點選&7一個方塊")
			);

	public static final Text MSG_GENERIC_ITEMINPUT = new Text(
			"MSG_GENERIC_ITEMINPUT", file,
			"en_US", "&7Press 'drop' when you're ready to import the item in your hand.",
			"fr_FR", "&7Appuyez sur 'jeter l'objet' lorsque vous serez prêt à importer l'item dans votre main.",
			"it_IT", Utils.asList("&7Premere 'drop' quando sei pronto a importare gli oggetti nella tua mano."),
			"es_ES", Utils.asList("&7Presione 'drop' cuando esté listo para importar el item de su mano."),
			"zh_TW", Utils.asList("&7將你要匯入的物品拿在手上，並按下鍵盤的 &fQ ")
			);

	public static final Text MSG_GENERIC_ITEMHEADDATABASEINPUT = new Text(
			"MSG_GENERIC_ITEMHEADDATABASEINPUT", file,
			"en_US", "&7Enter the HeadDatabase item ID in the chat or &ccancel &7to cancel.",
			"fr_FR", "&7Entrez l'ID de l'item HeadDatabase dans le chat ou &ccancel &7pour annuler.",
			"es_ES", Utils.asList("&7Escriba la ID del item de la base de datos de cabezas en el chat, o &ccancel"),
			"zh_TW", Utils.asList("&7輸入一個 &a物品ID &7，或是輸入 &ccancel &7取消")
			);

	public static final Text MSG_GENERIC_DELETEELEMENT = new Text(
			"MSG_GENERIC_DELETEELEMENT", file,
			"en_US", "&7Click on the element that you wish to delete (cancel by closing the GUI).",
			"fr_FR", "&7Cliquez sur l'élement que vous souhaitez supprimer (annulez en fermant le GUI).",
			"es_ES", Utils.asList("&7Haga click en el elemento que desea borrar (cancele mediante el cierre"),
			"zh_TW", Utils.asList("&7點擊圖示刪除你想刪除的物品，或是&c關閉GUI&7取消")
			);

	// items
	public static final Text GUI_GENERIC_EDITORITEMDELETESELF = new Text(
			"GUI_GENERIC_EDITORITEMDELETESELF", file,
			"en_US", "&6Reset",
			"fr_FR", "&6Réinitialiser",
			"it_IT", Utils.asList("&6Resetta"),
			"es_ES", Utils.asList("&6Reiniciar"),
			"zh_TW", Utils.asList("&6重置")
			);

	public static final Text GUI_GENERIC_EDITORITEMDELETESELFLORE = new Text(
			"GUI_GENERIC_EDITORITEMDELETESELFLORE", file,
			"en_US", Utils.asList("&c&lThis action is irreversible", "&cClick this icon to reset every", "&c setting of this element"),
			"fr_FR", Utils.asList("&c&lCette action est irréversible", "&cCliquez sur cette icône pour", "&c réinitialiser chaque paramètre de", "&c cet élément"),
			"it_IT", Utils.asList("&c&lQuesta azione è irreversibile", "&cClicca questa icona per resettare", "&ctutte le impostazioni di questo", "&celemento"),
			"es_ES", Utils.asList("&c&lEsta acción es irreversible!", "&cHaga click en el icono para reiniciar", "&c todas las configuraciones de este elemento"),
			"zh_TW", Utils.asList("&c&l這個動作是不可復原的", "&c點擊這個選項 &6重置所有", "&c在這個介面的選項")
			);

	public static final Text GUI_GENERIC_EDITORITEMBACK = new Text(
			"GUI_GENERIC_EDITORITEMBACK", file,
			"en_US", "&7Back",
			"fr_FR", "&7Retour",
			"it_IT", Utils.asList("&7Indietro"),
			"es_ES", Utils.asList("&7Volver"),
			"zh_TW", Utils.asList("&7上一頁")
			);

	public static final Text GUI_GENERIC_EDITORITEMADD = new Text(
			"GUI_GENERIC_EDITORITEMADD", file,
			"en_US", "&6Add",
			"fr_FR", "&6Ajouter",
			"it_IT", Utils.asList("&6Aggiungi"),
			"es_ES", Utils.asList("&6Añadir"),
			"zh_TW", Utils.asList("&6新增")
			);

	public static final Text GUI_GENERIC_EDITORITEMADDNUMBER = new Text(
			"GUI_GENERIC_EDITORITEMADDNUMBER", file,
			"en_US", "&6Add number",
			"fr_FR", "&6Ajouter un nombre",
			"es_ES", Utils.asList("&6Añadir número"),
			"zh_TW", Utils.asList("&6新增數字")
			);

	public static final Text GUI_GENERIC_EDITORITEMADDENTITYNAMED = new Text(
			"GUI_GENERIC_EDITORITEMADDENTITYNAMED", file,
			"en_US", "&6Add named entity",
			"fr_FR", "&6Ajouter une entité nommée",
			"es_ES", Utils.asList("&6Añadir el nombre de la entidad"),
			"zh_TW", Utils.asList("&6新增生物名稱")
			);

	public static final Text GUI_GENERIC_EDITORITEMDELETE = new Text(
			"GUI_GENERIC_EDITORITEMDELETE", file,
			"en_US", "&6Delete",
			"fr_FR", "&6Supprimer",
			"it_IT", Utils.asList("&6Elimina"),
			"es_ES", Utils.asList("&6Eliminar"),
			"zh_TW", Utils.asList("&6刪除")
			);

	public static final Text GUI_GENERIC_EDITORITEMDELETELORE = new Text(
			"GUI_GENERIC_EDITORITEMDELETELORE", file,
			"en_US", Utils.asList("&c&lThis action is irreversible", "&cClick this icon, then the element", "&c that you wish to delete"),
			"fr_FR", Utils.asList("&c&lCette action est irréversible", "&cCliquez sur cette icône, ensuite sur", "&c l'élement que vous souhaitez supprimer"),
			"it_IT", Utils.asList("&c&lQuesta azione è irreversibile", "&cClicca su questa icona, poi sull'elemento", "&c che desideri eliminare"),
			"es_ES", Utils.asList("&c&lEstá acción es irreversible!", "&cHaga click en el icono, y después en el elemento", "&c que desee borrar"),
			"zh_TW", Utils.asList("&c&l這個動作是不可復原的", "&c點擊這個選項 然後點擊", "&c在這個介面你要刪除的物品")
			);

	public static final Text GUI_GENERIC_EDITORCURRENTLORE = new Text(
			"GUI_GENERIC_EDITORCURRENTLORE", file,
			"en_US", Utils.asList("&7{description}", "", "&5Mandatory value : &d{mandatory}", "&5Value type : &d{type}", "", "&5Current value :", "&e{current}"),
			"fr_FR", Utils.asList("&7{description}", "", "", "&5Valeur obligatoire : &d{mandatory}", "&5Type de valeur : &d{type}", "", "&5Valeur actuelle :", "&e{current}"),
			"es_ES", Utils.asList("", "&5Valor requerido : &d{mandatory}", "&5Tipo de valor : &d{type}", "", "&5Valor actual :", "&e{current}"),
			"zh_TW", Utils.asList("", "&5必須要設定 : &d{mandatory}", "&5數值類型 : &d{type}", "", "&5當前數值 :", "&e{current}")
			);

	public static final Text GUI_GENERIC_EDITORVALUELORE = new Text(
			"GUI_GENERIC_EDITORCURRENTLORE", file,
			"en_US", Utils.asList("&7{description}", "", "&5Mandatory value : &d{mandatory}", "&5Value type : &d{type}", "", "&5Current value :", "&e{current}"),
			"fr_FR", Utils.asList("&7{description}", "", "", "&5Valeur obligatoire : &d{mandatory}", "&5Type de valeur : &d{type}", "", "&5Valeur actuelle :", "&e{current}"),
			"it_IT", Utils.asList("&7{description}", "", "&a&lClicca per modificare", "", "&5Valore obbligatorio : &d{value_mandatory}", "&5Tipo di valore: &d{value_type}", "", "&5Valore attuale:", "&e{value_current")
			);

	public static final Text GUI_GENERIC_EDITORVALUENODESCLORE = new Text(
			"GUI_GENERIC_EDITORCURRENTLORE", file,
			"en_US", Utils.asList("", "&5Mandatory value : &d{mandatory}", "&5Value type : &d{type}", "", "&5Current value :", "&e{current}"),
			"fr_FR", Utils.asList("", "", "&5Valeur obligatoire : &d{mandatory}", "&5Type de valeur : &d{type}", "", "&5Valeur actuelle :", "&e{current}"),
			"it_IT", Utils.asList("", "&a&lClicca per modificare", "", "&5Valore obbligatorio : &d{value_mandatory}", "&5Tipo di valore: &d{value_type}", "", "&5Valore attuale:", "&e{value_current")
			);

	public static final Text GUI_GENERIC_EDITORRAW = new Text(
			"GUI_GENERIC_EDITORRAW", file,
			"en_US", "&6Raw value",
			"fr_FR", "&6Valeur brute",
			"it_IT", Utils.asList("&6Valore originale"),
			"es_ES", Utils.asList("&6Valor original"),
			"zh_TW", Utils.asList("&6原始數值")
			);

	public static final Text GUI_GENERIC_EDITORRAWLORE = new Text(
			"GUI_GENERIC_EDITORRAWLORE", file,
			"en_US", Utils.asList("&7Edit the raw value (it will be parsed", "&7 for a player when needed)", "", "{placeholders}"),
			"fr_FR", Utils.asList("&7Éditer la valeur brute (elle sera", "&7 convertie pour un joueur quand nécessaire)", "", "{placeholders}"),
			"it_IT", Utils.asList("&7Modifica il valore originale (sarà analizzato", "&7per un giocatore quando sarà necessario)", "{placeholders}"),
			"es_ES", Utils.asList("&7Edite el valor original (será analizado para un", "&7 jugador cuando sea necesario)", "", "{placeholders}"),
			"zh_TW", Utils.asList("&7編輯原始數值", "", "{placeholders}")
			);

	public static final Text GUI_GENERIC_EDITORTYPERAW = new Text(
			"GUI_GENERIC_EDITORTYPERAW", file,
			"en_US", "&6Raw type value",
			"fr_FR", "&6Valeur brute de type",
			"it_IT", Utils.asList("&6Valore originale non trovato"),
			"es_ES", Utils.asList("&6Valor del tipo original"),
			"zh_TW", Utils.asList("&6原始數值類型")
			);

	public static final Text GUI_GENERIC_EDITORTEXTLINELORE = new Text(
			"GUI_GENERIC_EDITORTEXTLINELORE", file,
			"en_US", Utils.asList("&e{current}", "", "&7Edit the raw value (it will be parsed", "&7 for a player when needed)", "", "{placeholders}", "&7Change colors with &{code}", "", "&a&lLeft-click to edit", "&a&lRight-click to delete"),
			"fr_FR", Utils.asList("&e{current}", "", "&7Éditer la valeur brute (elle sera convertie pour un joueur quand nécessaire)", "", "{placeholders}", "&7Change colors with &{code}", "", "&a&lClic gauche pour éditer", "&a&lClic droit pour supprimer"),
			"it_IT", Utils.asList("&e{current}", "", "&7Modifica il valore originale (sarà analizzato ", "&7per un giocatore quando sarà necessario)", "{placeholders}", "", "&7Cambia il colore con &{code}", "", "&a&lTasto sinistro per modificare", "&a&lTasto destro per eliminare", "", "&a&l", "", "&5Valore obbligatorio: &d{value_mandatory}", "&5Tipo di valore: &d{value_type}", "", "&5Valore attuale:", "&d{value_current"),
			"es_ES", Utils.asList("&e{current}", "", "&7Edite el valor original (será analizado para", "&7 un jugador cuando sea necesario)", "", "{placeholders}", "&7Cambie los colores con &{code}", "", "&a&lHaga click izquierdo para editar", "&a&lHaga click derecho para borrar"),
			"zh_TW", Utils.asList("&e{current}", "", "&7編輯原始數值", "", "{placeholders}", "&7更改顏色使用 &{code}", "", "&f右鍵點擊 &a編輯", "&f左鍵點擊 &c刪除")
			);

	public static final Text GUI_GENERIC_EDITORLISTELEMENTLORE = new Text(
			"GUI_GENERIC_EDITORLISTELEMENTLORE", file,
			"en_US", Utils.asList("&e{current}", "", "&a&lLeft-click to edit", "&a&lRight-click to delete"),
			"fr_FR", Utils.asList("&e{current}", "", "&a&lClic gauche pour éditer", "&a&lClic droit pour supprimer"),
			"es_ES", Utils.asList("&e{current}", "", "&a&lHaga click izquierdo para editar", "&a&lHaga click derecho para borrar"),
			"zh_TW", Utils.asList("&e{current}", "", "&f右鍵點擊 &a編輯", "&f左鍵點擊 &c刪除")
			);

	public static final Text GUI_GENERIC_EDITORBOOLEANTOGGLE = new Text(
			"GUI_GENERIC_EDITORBOOLEANTOGGLE", file,
			"en_US", "&6Toggle",
			"fr_FR", "&6Basculer",
			"it_IT", Utils.asList("&6Disabilita"),
			"es_ES", Utils.asList("&6Cambiar"),
			"zh_TW", Utils.asList("&6切換")
			);

	public static final Text GUI_GENERIC_EDITORBOOLEANTOGGLELORE = new Text(
			"GUI_GENERIC_EDITORBOOLEANTOGGLELORE", file,
			"en_US", Utils.asList("&7Set the value to true/false"),
			"fr_FR", Utils.asList("&7Définir la valeur sur vrai/faux"),
			"it_IT", Utils.asList("&7Imposta il valore su vero/falso"),
			"es_ES", Utils.asList("&7Defina el valor como verdadero/falso"),
			"zh_TW", Utils.asList("&7將數值設定為 &atrue &7或是 &cfalse")
			);

	public static final Text GUI_GENERIC_EDITORNUMBERADD = new Text(
			"GUI_GENERIC_EDITORNUMBERADD", file,
			"en_US", "&a+{amount}",
			"es_ES", Utils.asList("&a+{amount}"),
			"zh_TW", Utils.asList("&a+{amount}")
			);

	public static final Text GUI_GENERIC_EDITORNUMBERTAKE = new Text(
			"GUI_GENERIC_EDITORNUMBERTAKE", file,
			"en_US", "&c-{amount}",
			"es_ES", Utils.asList("&c-{amount}"),
			"zh_TW", Utils.asList("&c-{amount}")
			);

	public static final Text GUI_GENERIC_EDITORNUMBERADDTAKELORE = new Text(
			"GUI_GENERIC_EDITORNUMBERADDTAKELORE", file,
			"en_US", Utils.asList("&7Click to apply"),
			"fr_FR", Utils.asList("&7Cliquez pour appliquer"),
			"es_ES", Utils.asList("&7Haga click para aplicar"),
			"zh_TW", Utils.asList("&7點擊新增")
			);

	public static final Text GUI_GENERIC_EDITORLOCATIONIMPORT = new Text(
			"GUI_GENERIC_EDITORLOCATIONIMPORT", file,
			"en_US", "&6Select",
			"fr_FR", "&6Sélectionner",
			"it_IT", Utils.asList("&6Seleziona"),
			"es_ES", Utils.asList("&6Seleccionar"),
			"zh_TW", Utils.asList("&6選擇")
			);

	public static final Text GUI_GENERIC_EDITORLOCATIONIMPORTLORE = new Text(
			"GUI_GENERIC_EDITORLOCATIONIMPORTLORE", file,
			"en_US", Utils.asList("&7Import a value (place or block)"),
			"fr_FR", Utils.asList("&7Importer une valeur (endroit ou bloc)"),
			"it_IT", Utils.asList("&7Importa un valore (place o blocco)"),
			"es_ES", Utils.asList("&7Importe el valor (colocar o bloquear)"),
			"zh_TW", Utils.asList("&7輸入一個數值")
			);

	public static final Text GUI_GENERIC_EDITORITEMIMPORT = new Text(
			"GUI_GENERIC_EDITORITEMIMPORT", file,
			"en_US", "&6Select",
			"fr_FR", "&6Sélectionner",
			"it_IT", Utils.asList("&6Seleziona"),
			"es_ES", Utils.asList("&6Seleccionar"),
			"zh_TW", Utils.asList("&6選擇")
			);

	public static final Text GUI_GENERIC_EDITORITEMIMPORTLORE = new Text(
			"GUI_GENERIC_EDITORITEMIMPORTLORE", file,
			"en_US", Utils.asList("&7Import a value (item)"),
			"fr_FR", Utils.asList("&7Importer une valeur (item)"),
			"it_IT", Utils.asList("&7Importa un valore (item)"),
			"es_ES", Utils.asList("&7Importe el valor (item)"),
			"zh_TW", Utils.asList("&7輸入一個數值 (物品)")
			);

	public static final Text GUI_GENERIC_EDITORITEMIMPORTHEADDATABASE = new Text(
			"GUI_GENERIC_EDITORITEMIMPORTHEADDATABASE", file,
			"en_US", "&6Select (HeadDatabase)",
			"fr_FR", "&6Sélectionner (HeadDatabase)",
			"it_IT", Utils.asList("&6Seleziona (HeadDatabase)"),
			"es_ES", Utils.asList("&6Seleccionar (HeadDatabase)"),
			"zh_TW", Utils.asList("&6選擇 (HeadDatabase)")
			);

	public static final Text GUI_GENERIC_EDITORITEMIMPORTHEADDATABASELORE = new Text(
			"GUI_GENERIC_EDITORITEMIMPORTHEADDATABASELORE", file,
			"en_US", Utils.asList("&7Import a value (item) from HeadDatabase"),
			"fr_FR", Utils.asList("&7Importer une valeur (item) from HeadDatabase"),
			"it_IT", Utils.asList("&7Importa un valore (item) from HeadDatabase"),
			"es_ES", Utils.asList("&7Importe el valor (item) de la base de datos de cabezas."),
			"zh_TW", Utils.asList("&7匯入一個數值 (物品)")
			);

	public static final Text GUI_GENERIC_EDITORENUMSELECT = new Text(
			"GUI_GENERIC_EDITORENUMSELECT", file,
			"en_US", "&6Select",
			"fr_FR", "&6Sélectionner",
			"it_IT", Utils.asList("&6Seleziona"),
			"es_ES", Utils.asList("&6Seleccionar"),
			"zh_TW", Utils.asList("&6選擇")
			);

	public static final Text GUI_GENERIC_EDITORENUMSELECTLORE = new Text(
			"GUI_GENERIC_EDITORENUMSELECTLORE", file,
			"en_US", Utils.asList("&7Select a regular value"),
			"fr_FR", Utils.asList("&7Sélectionner une valeur classique"),
			"it_IT", Utils.asList("&7Seleziona un valore regolare"),
			"es_ES", Utils.asList("&7Selccione un valor regular"),
			"zh_TW", Utils.asList("&7選擇一個正常的數值")
			);

	public static final Text GUI_GENERIC_EDITORENUMTYPESELECT = new Text(
			"GUI_GENERIC_EDITORENUMTYPESELECT", file,
			"en_US", "&6Select type",
			"fr_FR", "&6Sélectionner le type",
			"it_IT", Utils.asList("&6Seleziona il tipo"),
			"es_ES", Utils.asList("&6Seleccione el tipo"),
			"zh_TW", Utils.asList("&6選擇類型")
			);

	// editor : date
	public static final Text GUI_GENERIC_EDITOR_YEARLORE = new Text(
			"GUI_GENERIC_EDITOR_YEARLORE", file,
			"en_US", Utils.asList("&7Year"),
			"fr_FR", Utils.asList("&7Année"),
			"es_ES", Utils.asList("&7Año"),
			"zh_TW", Utils.asList("&7年")
			);

	public static final Text GUI_GENERIC_EDITOR_MONTHLORE = new Text(
			"GUI_GENERIC_EDITOR_MONTHLORE", file,
			"en_US", Utils.asList("&7Month"),
			"fr_FR", Utils.asList("&7Mois"),
			"es_ES", Utils.asList("&7Mes"),
			"zh_TW", Utils.asList("&7月")
			);

	public static final Text GUI_GENERIC_EDITOR_DAYLORE = new Text(
			"GUI_GENERIC_EDITOR_DAYLORE", file,
			"en_US", Utils.asList("&7Day of week"),
			"fr_FR", Utils.asList("&7Jour de la semaine"),
			"es_ES", Utils.asList("&7Día de la semana"),
			"zh_TW", Utils.asList("&7一周的一天")
			);

	public static final Text GUI_GENERIC_EDITOR_HOURLORE = new Text(
			"GUI_GENERIC_EDITOR_HOURLORE", file,
			"en_US", Utils.asList("&7Hour"),
			"fr_FR", Utils.asList("&7Heure"),
			"es_ES", Utils.asList("&7Hora"),
			"zh_TW", Utils.asList("&7小時")
			);

	public static final Text GUI_GENERIC_EDITOR_MINUTELORE = new Text(
			"GUI_GENERIC_EDITOR_MINUTELORE", file,
			"en_US", Utils.asList("&7Minute"),
			"es_ES", Utils.asList("&7Minuto"),
			"zh_TW", Utils.asList("&7分鐘")
			);

	public static final Text GUI_GENERIC_EDITOR_WEEKTIMEFRAMESTARTLORE = new Text(
			"GUI_GENERIC_EDITOR_WEEKTIMEFRAMESTARTLORE", file,
			"en_US", Utils.asList("&7Start of time frame in week"),
			"fr_FR", Utils.asList("&7Début de période dans la semaine"),
			"es_ES", Utils.asList("&7Comienzo del período en la semana"),
			"zh_TW", Utils.asList("&7一周內時間周期開始的時間")
			);

	public static final Text GUI_GENERIC_EDITOR_WEEKTIMEFRAMEENDLORE = new Text(
			"GUI_GENERIC_EDITOR_WEEKTIMEFRAMEENDLORE", file,
			"en_US", Utils.asList("&7End of time frame in week"),
			"fr_FR", Utils.asList("&7Fin de période dans la semaine"),
			"es_ES", Utils.asList("&7Final del período en la semana"),
			"zh_TW", Utils.asList("&7一周內時間周期結束的時間")
			);

	public static final Text GUI_GENERIC_EDITOR_CUTDELAYLORE = new Text(
			"GUI_GENERIC_EDITOR_CUTDELAYLORE", file,
			"en_US", Utils.asList("&7If this setting is specified, the time frame", "&7 will be split into smaller parts"),
			"fr_FR", Utils.asList("&7Si ce paramètre est spécifié, la période sera", "&7 divisée en plus petites parties"),
			"es_ES", Utils.asList("&7Si este parametro es especificado", "&7 el período será dividido en partes más pequéñas"),
			"zh_TW", Utils.asList("&7如果這個設定可用 時間周期", "&7會被 分成小部分")
			);
	public static final Text GUI_GENERIC_EDITOR_NPC_SHOWLORE = new Text(
			"GUI_GENERIC_EDITOR_NPC_SHOWLORE", file,
			"en_US", Utils.asList("&7Should the NPC be shown by default"),
			"fr_FR", Utils.asList("&7Est-ce que le NPC doit être montré par défaut"),
			"es_ES", Utils.asList("&7¿Debería mostrarse el NPC por defecto?"),
			"zh_TW", Utils.asList("&7哪一個NPC要做為預設顯示")
			);

	public static final Text GUI_GENERIC_EDITOR_NPC_NAMELORE = new Text(
			"GUI_GENERIC_EDITOR_NPC_NAMELORE", file,
			"en_US", Utils.asList("&7Name of the NPC"),
			"fr_FR", Utils.asList("&7Nom du NPC"),
			"es_ES", Utils.asList("&7Nombre del NPC"),
			"zh_TW", Utils.asList("&7NPC名稱")
			);

	public static final Text GUI_GENERIC_EDITOR_NPC_SKINDATALORE = new Text(
			"GUI_GENERIC_EDITOR_NPC_SKINDATALORE", file,
			"en_US", Utils.asList("&7Skin data of the NPC (can be generated", "&7 or obtained at https://mineskin.org/)"),
			"fr_FR", Utils.asList("&7Skin data de ce NPC (can be generated", "&7 or obtained at https://mineskin.org/)"),
			"es_ES", Utils.asList("&7Los datos de skin del NPC (pueden ser generados", "&7 o ser obtenidos en https://mineskin.org/)"),
			"zh_TW", Utils.asList("&7NPC的SKIN Data可以在下方網址找到", "&7heeps://mineskin.org/")
			);

	public static final Text GUI_GENERIC_EDITOR_NPC_SKINSIGNATURELORE = new Text(
			"GUI_GENERIC_EDITOR_NPC_SKINSIGNATURELORE", file,
			"en_US", Utils.asList("&7Skin signature of the NPC (can be generated", "&7 or obtained at https://mineskin.org/)"),
			"fr_FR", Utils.asList("&7Skin signature de ce NPC (can be generated", "&7 or obtained at https://mineskin.org/)"),
			"es_ES", Utils.asList("&7La firma de la skin del NPC (puede ser generada", "&7 o obtenida en https://mineskin.org/)"),
			"zh_TW", Utils.asList("&7NPC的SKIN Signature可以在下方網址找到", "&7heeps://mineskin.org/")
			);

	public static final Text GUI_GENERIC_EDITOR_NPC_LOCATIONLORE = new Text(
			"GUI_GENERIC_EDITOR_NPC_LOCATIONLORE", file,
			"en_US", Utils.asList("&7Default location of the NPC"),
			"fr_FR", Utils.asList("&7Emplacement par défaut du NPC"),
			"es_ES", Utils.asList("&7Localización por defecto del NPC"),
			"zh_TW", Utils.asList("&7NPC的預設位置")
			);

	public static final Text GUI_GENERIC_EDITOR_NPC_TARGETDISTANCELORE = new Text(
			"GUI_GENERIC_EDITOR_NPC_TARGETDISTANCELORE", file,
			"en_US", Utils.asList("&7Distance under which the NPC will", "&7 look at the player when close"),
			"fr_FR", Utils.asList("&7Distance à laquelle le NPC regardera", "&7 le joueur s'il en est proche"),
			"es_ES", Utils.asList("&7Distancia desde la cual el NPC", "&7Va a a mirar al jugador cercano"),
			"zh_TW", Utils.asList("&7設定一個距離 當玩家進入這個距離時", "&7NPC將會看向那個玩家")
			);

	public static final Text GUI_GENERIC_EDITOR_NPC_ATTACKBEHAVIORLORE = new Text(
			"GUI_GENERIC_EDITOR_NPC_ATTACKBEHAVIORLORE", file,
			"en_US", Utils.asList("&7Attack behavior towards other entities"),
			"fr_FR", Utils.asList("&7Comportement d'attaque envers les autres entités"),
			"es_ES", Utils.asList("&7Comportamiento de ataque a otras entidades"),
			"zh_TW", Utils.asList("&7對其他實體的攻擊行為")
			);

	public static final Text GUI_GENERIC_EDITOR_NPC_ATTACKDISTANCELORE = new Text(
			"GUI_GENERIC_EDITOR_NPC_ATTACKDISTANCELORE", file,
			"en_US", Utils.asList("&7Distance under which the NPC will", "&7 try to attack the player when close"),
			"fr_FR", Utils.asList("&7Distance à laquelle le NPC essaiera", "&7 d'attaquer le joueur s'il en est proche"),
			"es_ES", Utils.asList("&7Distancia a la cual el NPC intentara", "&7 atacar a un jugador cercano"),
			"zh_TW", Utils.asList("&7設定一個距離 當玩家進入這個距離時", "&7NPC將會攻擊那個玩家")
			);

	public static final Text GUI_GENERIC_EDITOR_NPC_STATUSLORE = new Text(
			"GUI_GENERIC_EDITOR_NPC_STATUSLORE", file,
			"en_US", Utils.asList("&7List of status of the NPC"),
			"fr_FR", Utils.asList("&7Liste de status du NPC"),
			"es_ES", Utils.asList("&7Lista del estado de los NPC"),
			"zh_TW", Utils.asList("&7列出NPC可設定的狀態")
			);

	public static final Text GUI_GENERIC_EDITOR_NPC_STUFFLORE = new Text(
			"GUI_GENERIC_EDITOR_NPC_STUFFLORE", file,
			"en_US", Utils.asList("&7Stuff by default of the NPC"),
			"fr_FR", Utils.asList("&7Équipement par défaut du NPC"),
			"es_ES", Utils.asList("&7Equipamiento por defecto del NPC"),
			"zh_TW", Utils.asList("&7NPC的預設裝備/物品")
			);

	public static final Text GUI_GENERIC_EDITOR_NPC_VARIABLESLORE = new Text(
			"GUI_GENERIC_EDITOR_NPC_VARIABLESLORE", file,
			"en_US", Utils.asList("&7Default variables values for the NPC"),
			"fr_FR", Utils.asList("&7Variables par défaut pour le NPC"),
			"es_ES", Utils.asList("&7Valores de variables por defecto para el NPC"),
			"zh_TW", Utils.asList("&7NPC可以自然觸發的行為")
			);

	// editor : block setting
	public static final Text GUI_GENERIC_EDITOR_BLOCKTYPELORE = new Text(
			"GUI_GENERIC_EDITOR_BLOCKTYPELORE", file,
			"en_US", Utils.asList("&7Type of block"),
			"fr_FR", Utils.asList("&7Type de bloc"),
			"it_IT", Utils.asList("&7Tipo di blocco"),
			"es_ES", Utils.asList("&7Tipo de bloque"),
			"zh_TW", Utils.asList("&7方塊種類")
			);

	// editor : mob setting
	public static final Text GUI_GENERIC_EDITOR_MOB_TYPELORE = new Text(
			"GUI_GENERIC_EDITOR_MOB_TYPELORE", file,
			"en_US", Utils.asList("&7Type of mob"),
			"fr_FR", Utils.asList("&7Type de mob"),
			"it_IT", Utils.asList("&7Tipo di mob"),
			"es_ES", Utils.asList("&7Tipo de mob"),
			"zh_TW", Utils.asList("&7怪物種類")
			);

	public static final Text GUI_GENERIC_EDITOR_MOB_NAMELORE = new Text(
			"GUI_GENERIC_EDITOR_MOB_NAMELORE", file,
			"en_US", Utils.asList("&7Name of mob"),
			"fr_FR", Utils.asList("&7Nom de mob"),
			"it_IT", Utils.asList("&7Nome del mob"),
			"es_ES", Utils.asList("&7Nombre del mob"),
			"zh_TW", Utils.asList("&7怪物名稱")
			);

	public static final Text GUI_GENERIC_EDITOR_MOB_COLORLORE = new Text(
			"GUI_GENERIC_EDITOR_MOB_COLORLORE", file,
			"en_US", Utils.asList("&7Color of the mob (if applicable)"),
			"fr_FR", Utils.asList("&7Couleur du mob (si applicable)"),
			"es_ES", Utils.asList("&7Color del mob (si es aplicable)"),
			"zh_TW", Utils.asList("&7怪物的顏色 (如果可用)")
			);

	public static final Text GUI_GENERIC_EDITOR_MOB_AMOUNTLORE = new Text(
			"GUI_GENERIC_EDITOR_MOB_AMOUNTLORE", file,
			"en_US", Utils.asList("&7Mob amount"),
			"fr_FR", Utils.asList("&7Nombre de mobs"),
			"es_ES", Utils.asList("&7Número de mobs"),
			"zh_TW", Utils.asList("&7怪物數量")
			);

	// editor : enchantment setting
	public static final Text GUI_GENERIC_EDITOR_ENCHANTMENT_TYPELORE = new Text(
			"GUI_GENERIC_EDITOR_ENCHANTMENT_TYPELORE", file,
			"en_US", Utils.asList("&7Type of enchantment"),
			"fr_FR", Utils.asList("&7Type d'enchantement"),
			"es_ES", Utils.asList("&7Tipo de encantamiento"),
			"zh_TW", Utils.asList("&7附魔種類")
			);

	public static final Text GUI_GENERIC_EDITOR_ENCHANTMENT_LEVELLORE = new Text(
			"GUI_GENERIC_EDITOR_ENCHANTMENT_LEVELLORE", file,
			"en_US", Utils.asList("&7Level of enchantment"),
			"fr_FR", Utils.asList("&7Niveau d'enchantement"),
			"es_ES", Utils.asList("&7Nivel del encantamiento"),
			"zh_TW", Utils.asList("&7附魔等擊")
			);

	// editor : potion effect setting
	public static final Text GUI_GENERIC_EDITOR_POTIONEFFECT_TYPELORE = new Text(
			"GUI_GENERIC_EDITOR_POTIONEFFECT_TYPELORE", file,
			"en_US", Utils.asList("&7Type of effect"),
			"fr_FR", Utils.asList("&7Type d'effet"),
			"it_IT", Utils.asList("&7Tipo d'effetto"),
			"es_ES", Utils.asList("&7Tipo de efecto"),
			"zh_TW", Utils.asList("&7藥水效果種類")
			);

	public static final Text GUI_GENERIC_EDITOR_POTIONEFFECT_AMPLIFIERLORE = new Text(
			"GUI_GENERIC_EDITOR_POTIONEFFECT_AMPLIFIERLORE", file,
			"en_US", Utils.asList("&7Amplifier of effect"),
			"fr_FR", Utils.asList("&7Amplificateur d'effet"),
			"es_ES", Utils.asList("&7Amplificador del efecto"),
			"zh_TW", Utils.asList("&7藥水效果等擊")
			);

	public static final Text GUI_GENERIC_EDITOR_POTIONEFFECT_DURATIONLORE = new Text(
			"GUI_GENERIC_EDITOR_POTIONEFFECT_DURATIONLORE", file,
			"en_US", Utils.asList("&7Duration of effect (in ticks, 1 sec = 20 ticks)"),
			"fr_FR", Utils.asList("&7Durée de l'effet (en ticks, 1 sec = 20 ticks)"),
			"it_IT", Utils.asList("&7Durata dell'effetto (in tick, 1 sec = 20 ticks)"),
			"es_ES", Utils.asList("&7Duración del efecto (en ticks, 1 seg = 20 ticks)"),
			"zh_TW", Utils.asList("&7藥水效果持續時間 (遊戲刻，1秒 = 20 遊戲刻)")
			);

	// editor : sound setting
	public static final Text GUI_GENERIC_EDITOR_SOUND_TYPELORE = new Text(
			"GUI_GENERIC_EDITOR_SOUND_TYPELORE", file,
			"en_US", Utils.asList("&7Type of sound"),
			"fr_FR", Utils.asList("&7Type de son"),
			"it_IT", Utils.asList("&7Tipo di suono"),
			"es_ES", Utils.asList("&7Tipo de sonido"),
			"zh_TW", Utils.asList("&7聲音類型")
			);

	public static final Text GUI_GENERIC_EDITOR_SOUND_VOLUMELORE = new Text(
			"GUI_GENERIC_EDITOR_SOUND_VOLUMELORE", file,
			"en_US", Utils.asList("&7Volume of sound (normal = 1.0)"),
			"fr_FR", Utils.asList("&7Volume du son (normal = 1.0)"),
			"it_IT", Utils.asList("&7Volume del suono (normale = 1.0)"),
			"es_ES", Utils.asList("&7Volumen del sonido (normal= 1.0)"),
			"zh_TW", Utils.asList("&7聲音大小 (預設 = 1.0)")
			);

	public static final Text GUI_GENERIC_EDITOR_SOUND_PITCH = new Text(
			"GUI_GENERIC_EDITOR_SOUND_PITCH", file,
			"en_US", Utils.asList("&7Pitch of sound (normal = 1.0)"),
			"fr_FR", Utils.asList("&7Pitch du son (normal = 1.0)"),
			"it_IT", Utils.asList("&7Passo del suono (normale = 1.0)"),
			"es_ES", Utils.asList("&7Tono del sonido (normal = 1.0)"),
			"zh_TW", Utils.asList("&7聲音音高 (預設 = 1.0)")
			);

	// editor : tab setting
	public static final Text GUI_GENERIC_EDITOR_TAB_HEADERLORE = new Text(
			"GUI_GENERIC_EDITOR_TAB_HEADERLORE", file,
			"en_US", Utils.asList("&7Header"),
			"fr_FR", Utils.asList("&7En-tête"),
			"it_IT", Utils.asList("&7Intestazione"),
			"es_ES", Utils.asList("&7Título"),
			"zh_TW", Utils.asList("&7TAB上排")
			);

	public static final Text GUI_GENERIC_EDITOR_TAB_FOOTERLORE = new Text(
			"GUI_GENERIC_EDITOR_TAB_FOOTERLORE", file,
			"en_US", Utils.asList("&7Footer"),
			"fr_FR", Utils.asList("&7Bas de page"),
			"it_IT", Utils.asList("&7Fondo della pagina"),
			"es_ES", Utils.asList("&7Pie de página"),
			"zh_TW", Utils.asList("&7TAB下排")
			);

	// editor : title setting
	public static final Text GUI_GENERIC_EDITOR_TITLE_TITLELORE = new Text(
			"GUI_GENERIC_EDITOR_TITLE_TITLELORE", file,
			"en_US", Utils.asList("&7Title"),
			"fr_FR", Utils.asList("&7Titre"),
			"it_IT", Utils.asList("&7Titolo"),
			"es_ES", Utils.asList("&7Título"),
			"zh_TW", Utils.asList("&7標題")
			);

	public static final Text GUI_GENERIC_EDITOR_TITLE_SUBTITLELORE = new Text(
			"GUI_GENERIC_EDITOR_TITLE_SUBTITLELORE", file,
			"en_US", Utils.asList("&7Subtitle"),
			"fr_FR", Utils.asList("&7Sous-titre"),
			"it_IT", Utils.asList("&7Sottotitolo"),
			"es_ES", Utils.asList("&7Subtítulo"),
			"zh_TW", Utils.asList("&7子標題")
			);

	public static final Text GUI_GENERIC_EDITOR_TITLE_FADEINLORE = new Text(
			"GUI_GENERIC_EDITOR_TITLE_FADEINLORE", file,
			"en_US", Utils.asList("&7Fade in (in ticks) (1 sec = 20 ticks)"),
			"fr_FR", Utils.asList("&7Temps d'apparition (en ticks) (1 sec = 20 ticks)"),
			"it_IT", Utils.asList("&7Tempo d'attesa (in tick) (1 sec = 20 ticks)"),
			"es_ES", Utils.asList("&7Tiempo hasta desaparecer (en ticks) (1 seg = 20 ticks)"),
			"zh_TW", Utils.asList("&7淡入時間 (遊戲刻，1秒 = 20 遊戲刻)")
			);

	public static final Text GUI_GENERIC_EDITOR_TITLE_DURATIONLORE = new Text(
			"GUI_GENERIC_EDITOR_TITLE_DURATIONLORE", file,
			"en_US", Utils.asList("&7Duration (in ticks) (1 sec = 20 ticks)"),
			"fr_FR", Utils.asList("&7Durée (en ticks) (1 sec = 20 ticks)"),
			"it_IT", Utils.asList("&7Durata (in ticks) (1 sec = 20 ticks)"),
			"es_ES", Utils.asList("&7Duración (en ticks) (1 seg = 20 ticks)"),
			"zh_TW", Utils.asList("&7持續時間 (遊戲刻，1秒 = 20 遊戲刻)")
			);

	public static final Text GUI_GENERIC_EDITOR_TITLE_FADEOUTLORE = new Text(
			"GUI_GENERIC_EDITOR_TITLE_FADEOUTLORE", file,
			"en_US", Utils.asList("&7Fade out (in ticks) (1 sec = 20 ticks)"),
			"fr_FR", Utils.asList("&7Temps de disparition (en ticks) (1 sec = 20 ticks)"),
			"it_IT", Utils.asList("&7Tempo di scomparsa (in ticks) (1 sec = 20 ticks)"),
			"es_ES", Utils.asList("&7Duración (en ticks) (1 seg = 20 ticks)"),
			"zh_TW", Utils.asList("&7淡出時間 (遊戲刻，1秒 = 20 遊戲刻)")
			);

	// editor : item setting
	public static final Text GUI_GENERIC_EDITOR_ITEM_SLOTLORE = new Text(
			"GUI_GENERIC_EDITOR_ITEM_SLOTLORE", file,
			"en_US", Utils.asList("&7Slot in the GUI"),
			"fr_FR", Utils.asList("&7Position dans le GUI"),
			"it_IT", Utils.asList("&7Posizione nella GUI"),
			"es_ES", Utils.asList("&7Lugar en la GUI"),
			"zh_TW", Utils.asList("&7物品在GUI的位置")
			);

	public static final Text GUI_GENERIC_EDITOR_ITEM_CHANCELORE = new Text(
			"GUI_GENERIC_EDITOR_ITEM_CHANCELORE", file,
			"en_US", Utils.asList("&7Item apparition chance"),
			"fr_FR", Utils.asList("&7Chance pour l'item d'apparaître"),
			"it_IT", Utils.asList("&7Cambia il tempo d'attesa"),
			"es_ES", Utils.asList("&7Posibilidad de aparición del item"),
			"zh_TW", Utils.asList("&7物品出現機率")
			);

	public static final Text GUI_GENERIC_EDITOR_ITEM_MAXAMOUNTLORE = new Text(
			"GUI_GENERIC_EDITOR_ITEM_MAXAMOUNTLORE", file,
			"en_US", Utils.asList("&7Maximum items amount"),
			"fr_FR", Utils.asList("&7Nombre d'items maximum"),
			"it_IT", Utils.asList("&7Numero massimo di item"),
			"es_ES", Utils.asList("&7Número máximo por item"),
			"zh_TW", Utils.asList("&7最大物品數量")
			);

	public static final Text GUI_GENERIC_EDITOR_ITEM_ENABLEDLORE = new Text(
			"GUI_GENERIC_EDITOR_ITEM_ENABLEDLORE", file,
			"en_US", Utils.asList("&7Is the item enabled"),
			"fr_FR", Utils.asList("&7Est-ce que l'item est activé"),
			"it_IT", Utils.asList("&7È l'oggetto attivo"),
			"es_ES", Utils.asList("&7¿Está activo el item?"),
			"zh_TW", Utils.asList("&7物品是否開啟")
			);

	public static final Text GUI_GENERIC_EDITOR_ITEM_TYPELORE = new Text(
			"GUI_GENERIC_EDITOR_ITEM_TYPELORE", file,
			"en_US", Utils.asList("&7Type of item"),
			"fr_FR", Utils.asList("&7Type d'item"),
			"it_IT", Utils.asList("&7Tipo di oggetto"),
			"es_ES", Utils.asList("&7Tipo de item"),
			"zh_TW", Utils.asList("&7物品的類型")
			);

	public static final Text GUI_GENERIC_EDITOR_ITEM_DURABILITYLORE = new Text(
			"GUI_GENERIC_EDITOR_ITEM_DURABILITYLORE", file,
			"en_US", Utils.asList("&7Durability of item"),
			"fr_FR", Utils.asList("&7Durabilité de l'item"),
			"it_IT", Utils.asList("&7Durabilità dell'oggetto"),
			"es_ES", Utils.asList("&7Durabilidad del item"),
			"zh_TW", Utils.asList("&7物品的耐久度")
			);

	public static final Text GUI_GENERIC_EDITOR_ITEM_UNBREAKABLELORE = new Text(
			"GUI_GENERIC_EDITOR_ITEM_UNBREAKABLELORE", file,
			"en_US", Utils.asList("&7Does the item has the unbreakable tag"),
			"fr_FR", Utils.asList("&7Est-ce que l'item a le tag unbreakable"),
			"it_IT", Utils.asList("&7L'oggetto ha il tag unbreakable"),
			"es_ES", Utils.asList("&7¿Es irrompible el item?"),
			"zh_TW", Utils.asList("&7物品是否有 不可破壞 的標籤")
			);

	public static final Text GUI_GENERIC_EDITOR_ITEM_AMOUNTLORE = new Text(
			"GUI_GENERIC_EDITOR_ITEM_AMOUNTLORE", file,
			"en_US", Utils.asList("&7Amount of items"),
			"fr_FR", Utils.asList("&7Nombre d'items"),
			"it_IT", Utils.asList("&7Numero di oggetti"),
			"es_ES", Utils.asList("&7Número de items"),
			"zh_TW", Utils.asList("&7物品的數量")
			);

	public static final Text GUI_GENERIC_EDITOR_ITEM_LORE = new Text(
			"GUI_GENERIC_EDITOR_ITEM_LORE", file,
			"en_US", Utils.asList("&7Display name of item"),
			"fr_FR", Utils.asList("&7Nom d'affichage de l'item"),
			"it_IT", Utils.asList("&7Mostra il nome dell'oggetto"),
			"es_ES", Utils.asList("&7Mostrar el nombre del item"),
			"zh_TW", Utils.asList("&7物品的顯示名稱")
			);

	public static final Text GUI_GENERIC_EDITOR_ITEM_LORELORE = new Text(
			"GUI_GENERIC_EDITOR_ITEM_LORELORE", file,
			"en_US", Utils.asList("&7Lore of item"),
			"fr_FR", Utils.asList("&7Description de l'item"),
			"it_IT", Utils.asList("&7Descrizione dell'oggetto"),
			"es_ES", Utils.asList("&7Lore del item"),
			"zh_TW", Utils.asList("&7物品的介紹")
			);

	public static final Text GUI_GENERIC_EDITOR_ITEM_ENCHANTSLORE = new Text(
			"GUI_GENERIC_EDITOR_ITEM_ENCHANTSLORE", file,
			"en_US", Utils.asList("&7List of item enchants"),
			"fr_FR", Utils.asList("&7Liste des enchantements de l'item"),
			"it_IT", Utils.asList("&7Lista di incantesimi dell'oggetto"),
			"es_ES", Utils.asList("&7Lista de encantamientos para el item"),
			"zh_TW", Utils.asList("&7物品的附魔")
			);

	public static final Text GUI_GENERIC_EDITOR_ITEM_EFFECTSLORE = new Text(
			"GUI_GENERIC_EDITOR_ITEM_EFFECTSLORE", file,
			"en_US", Utils.asList("&7List of item effects"),
			"fr_FR", Utils.asList("&7Liste des effets de l'item"),
			"es_ES", Utils.asList("&7Lista de efectos para el item"),
			"zh_TW", Utils.asList("&7物品的藥水效果")
			);

	public static final Text GUI_GENERIC_EDITOR_ITEM_NBTLORE = new Text(
			"GUI_GENERIC_EDITOR_ITEM_NBTLORE", file,
			"en_US", Utils.asList("&7NBT tag of item, base64 encoded"),
			"fr_FR", Utils.asList("&7NBT tag de l'item, encodé en base64"),
			"it_IT", Utils.asList("&7NBT tag dell'oggetto, base64 encoded"),
			"es_ES", Utils.asList("&7NBT tag del item"),
			"zh_TW", Utils.asList("&7物品的 NBT標籤 基礎為64")
			);

	public static final Text GUI_GENERIC_EDITOR_ITEM_MUSTHAVEINHANDLORE = new Text(
			"GUI_GENERIC_EDITOR_ITEM_MUSTHAVEINHANDLORE", file,
			"en_US", Utils.asList("&7Should the item be hold in hand"),
			"fr_FR", Utils.asList("&7Est-ce que l'item doit être tenu en main"),
			"it_IT", Utils.asList("&7Se l'oggetto si tiene in mano"),
			"es_ES", Utils.asList("&7¿Debería el item estar sujeto en la mano?"),
			"zh_TW", Utils.asList("&7物品是否要拿在手上")
			);

	public static final Text GUI_GENERIC_EDITOR_ITEM_REMOVEAFTERACTIONLORE = new Text(
			"GUI_GENERIC_EDITOR_ITEM_REMOVEAFTERACTIONLORE", file,
			"en_US", Utils.asList("&7Should the item be removed after action"),
			"fr_FR", Utils.asList("&7Est-ce que l'item doit être supprimé après l'action"),
			"it_IT", Utils.asList("&7Se l'oggetto deve essere cancellato dopo l'azione"),
			"es_ES", Utils.asList("&7¿Debería eliminarse el item después de la acción?"),
			"zh_TW", Utils.asList("&7物品是否要在動作執行後移除")
			);

	public static final Text GUI_GENERIC_EDITOR_ITEM_CHECKDURABILITYLORE = new Text(
			"GUI_GENERIC_EDITOR_ITEM_CHECKDURABILITYLORE", file,
			"en_US", Utils.asList("&7Should the durability be the exact same"),
			"fr_FR", Utils.asList("&7Est-ce que la durabilité doit être exactement la même"),
			"es_ES", Utils.asList("&7¿Debería mantenerse la durabilidad?"),
			"zh_TW", Utils.asList("&7物品的耐久度是否要完全一樣")
			);

	public static final Text GUI_GENERIC_EDITOR_ITEM_EXACTMATCHLORE = new Text(
			"GUI_GENERIC_EDITOR_ITEM_EXACTMATCHLORE", file,
			"en_US", Utils.asList("&7Should the item be exactly the same or", "&7 can it has more enchants, name, ..."),
			"fr_FR", Utils.asList("&7Est-ce que l'item doit être exactement le", "&7 même ou est-ce qu'il peut avoir des", "&7 enchantements, nom, en plus..."),
			"es_ES", Utils.asList("&7¿Debería ser exactamente el mismo item o ", "&7 puede tener mas encantamientos, nombres, ...?"),
			"zh_TW", Utils.asList("&7物品是否要完全一樣", "&7是否可以多一些附魔、名稱...")
			);

	public static final Text GUI_GENERIC_EDITOR_ITEM_HIDEFLAGSLORE = new Text(
			"GUI_GENERIC_EDITOR_ITEM_HIDEFLAGSLORE", file,
			"en_US", Utils.asList("&7Should the flags of the item be hidden"),
			"fr_FR", Utils.asList("&7Est-ce que les flags de l'item doivent être cachés"),
			"es_ES", Utils.asList("&7¿Debería las flags del item estar ocultas?"),
			"zh_TW", Utils.asList("&7是否隱藏物品的標籤")
			);

	// editor : npc settings
	public static final Text GUI_GCORE_EDITOR_NPC_BEHAVIORTRIGGERSLORE = new Text(
			"GUI_GCORE_EDITOR_NPC_BEHAVIORTRIGGERSLORE", file,
			"en_US", Utils.asList("&7A list of triggers that'll activate the behavior"),
			"fr_FR", Utils.asList("&7Une liste de triggers qui activeront le behavior")
			);

	public static final Text GUI_GCORE_EDITOR_NPC_BEHAVIORPROCESSESLORE = new Text(
			"GUI_GCORE_EDITOR_NPC_BEHAVIORPROCESSESLORE", file,
			"en_US", Utils.asList("&7A list of processes for this behavior", "&7You select which ones will be run in the events", "&7 or in the processes themselves"),
			"fr_FR", Utils.asList("&7Une liste de processus pour ce behavior", "&7Vous définissez lesquels seront lancés dans les events", "&7 ou directement dans les processus"),
			"es_ES", Utils.asList("&7Una lista de procesos para este comportamiento", "&7Selecciona cuáles se ejecutarán en los eventos", "&7 o en los procesos mismos"),
			"zh_TW", Utils.asList("&7列出此行為的流程", "&7選擇哪一個行為要在哪個事件", "&7或是在流程本身運行")
			);

	public static final Text GUI_GCORE_EDITOR_NPC_BEHAVIORACTIONSLORE = new Text(
			"GUI_GCORE_EDITOR_NPC_BEHAVIORACTIONSLORE", file,
			"en_US", Utils.asList("&7A list of actions for this behavior", "&7You select which ones will be run in the processes"),
			"fr_FR", Utils.asList("&7Une liste d'actions pour ce behavior", "&7Vous définissez lesquelles seront lancées dans les processus"),
			"es_ES", Utils.asList("&7Una lista de acciones para este comportamiento", "&7Usted selecciona cuales de ellos se van a ejecutar en los procesos"),
			"zh_TW", Utils.asList("&7列出此行為的動作", "&7選擇哪一個動作要運行")
			);

	public static final Text GUI_GCORE_EDITOR_NPC_BEHAVIORCONDITIONSLORE = new Text(
			"GUI_GCORE_EDITOR_NPC_BEHAVIORCONDITIONSLORE", file,
			"en_US", Utils.asList("&7A list of conditions for this behavior", "&7You select which ones will be checked in the processes"),
			"fr_FR", Utils.asList("&7Une liste de conditions pour ce behavior", "&7Vous définissez lesquelles seront vérifiées dans les processus"),
			"es_ES", Utils.asList("&7Una lista de condiciones para este comportamiento", "&7Usted selecciona cuales de ellos serán verificados en los procesos"),
			"zh_TW", Utils.asList("&7列出此行為的條件", "&7選出哪一個條件在運行時會被查看")
			);

	public static final Text GUI_GCORE_EDITOR_NPC_BEHAVIORMAXCONCURRENTINSTANCESLORE = new Text(
			"GUI_GCORE_EDITOR_NPC_BEHAVIORMAXCONCURRENTINSTANCESLORE", file,
			"en_US", Utils.asList("&7How many instances of this behavior", "&7 can be active at the same time"),
			"fr_FR", Utils.asList("&7Combien d'instances de ce behavior", "&7 peuvent être actives en même temps"),
			"es_ES", Utils.asList("&7¿Cuantas instancias puede tener este comportamiento", "&7 activas al mismo tiempo?"),
			"zh_TW", Utils.asList("&7列出此行為的例子", "&7是可以同時進行的")
			);

	public static final Text GUI_GCORE_EDITOR_NPC_BEHAVIOREVENTTYPELORE = new Text(
			"GUI_GCORE_EDITOR_NPC_BEHAVIOREVENTTYPELORE", file,
			"en_US", Utils.asList("&7Type of event"),
			"fr_FR", Utils.asList("&7Type d'event"),
			"es_ES", Utils.asList("&7Tipo de evento"),
			"zh_TW", Utils.asList("&7事件種類")
			);

	public static final Text GUI_GCORE_EDITOR_NPC_BEHAVIOREVENTPROCESSLORE = new Text(
			"GUI_GCORE_EDITOR_NPC_BEHAVIOREVENTPROCESSLORE", file,
			"en_US", Utils.asList("&7What process should be run when this event is triggered"),
			"fr_FR", Utils.asList("&7Quel processus doit être lancé lorsque cet event est activé"),
			"es_ES", Utils.asList("&7¿Qué tipo de procesos se deberían activar caundo se activa este evento?")
			);

	public static final Text GUI_GCORE_EDITOR_NPC_BEHAVIORACTIONTYPELORE = new Text(
			"GUI_GCORE_EDITOR_NPC_BEHAVIORACTIONTYPELORE", file,
			"en_US", Utils.asList("&7Type of action"),
			"fr_FR", Utils.asList("&7Type d'action"),
			"es_ES", Utils.asList("&7Tipo de acción"),
			"zh_TW", Utils.asList("&7動作類型")
			);

	public static final Text GUI_GCORE_EDITOR_NPC_BEHAVIORCONDITIONTYPELORE = new Text(
			"GUI_GCORE_EDITOR_NPC_BEHAVIORCONDITIONTYPELORE", file,
			"en_US", Utils.asList("&7Type of condition"),
			"fr_FR", Utils.asList("&7Type de condition"),
			"es_ES", Utils.asList("&7Tipo de condición"),
			"zh_TW", Utils.asList("&7條件類型")
			);

	public static final Text GUI_GCORE_EDITOR_NPC_BEHAVIORPLAYERDISTANCEVALUELORE = new Text(
			"GUI_GCORE_EDITOR_NPC_BEHAVIORPLAYERDISTANCEVALUELORE", file,
			"en_US", Utils.asList("&7To which value the distance must be compared"),
			"fr_FR", Utils.asList("&7À quelle valeur la distance doit être comparée"),
			"es_ES", Utils.asList("&7A qué distancia se debe comparar el valor de la distancia"),
			"zh_TW", Utils.asList("&7哪一個數值要與距離比較")
			);

	public static final Text GUI_GCORE_EDITOR_NPC_BEHAVIORRANDOMVALUELORE = new Text(
			"GUI_GCORE_EDITOR_NPC_BEHAVIORRANDOMVALUELORE", file,
			"en_US", Utils.asList("&7To which value the generated number must be compared"),
			"fr_FR", Utils.asList("&7À quelle valeur le nombre généré doit être comparée"),
			"es_ES", Utils.asList("&7A que valor se debe comparar el número generado"),
			"zh_TW", Utils.asList("&7哪一個數值要與生成的數字比較")
			);

	public static final Text GUI_GCORE_EDITOR_NPC_BEHAVIORRANDOMMINLORE = new Text(
			"GUI_GCORE_EDITOR_NPC_BEHAVIORRANDOMMINLORE", file,
			"en_US", Utils.asList("&7The minimum value that will be generated"),
			"fr_FR", Utils.asList("&7La valeur minimum qui sera générée"),
			"es_ES", Utils.asList("&7El valor mínimo que será generado"),
			"zh_TW", Utils.asList("&7生成的最小數值")
			);

	public static final Text GUI_GCORE_EDITOR_NPC_BEHAVIORRANDOMMAXLORE = new Text(
			"GUI_GCORE_EDITOR_NPC_BEHAVIORRANDOMMAXLORE", file,
			"en_US", Utils.asList("&7The maximum value that will be generated"),
			"fr_FR", Utils.asList("&7La valeur maximale qui sera générée"),
			"es_ES", Utils.asList("&7El valor máximo que será generado"),
			"zh_TW", Utils.asList("&7生成的最大數值")
			);

	public static final Text GUI_GCORE_EDITOR_NPC_BEHAVIORVARIABLELORE = new Text(
			"GUI_GCORE_EDITOR_NPC_BEHAVIORACTIONVARIABLELORE", file,
			"en_US", Utils.asList("&7Which variable must be changed"),
			"fr_FR", Utils.asList("&7Quelle variable doit être changée")
			);

	public static final Text GUI_GCORE_EDITOR_NPC_BEHAVIORVARIABLEVALUELORE = new Text(
			"GUI_GCORE_EDITOR_NPC_BEHAVIORVARIABLEVALUELORE", file,
			"en_US", Utils.asList("&7To which value the variable must be changed"),
			"fr_FR", Utils.asList("&7À quelle valeur la variable doit être changée"),
			"es_ES", Utils.asList("&7A que valor se debe cambiar la variable"),
			"zh_TW", Utils.asList("&7要改變哪一個數值的參數")
			);

	public static final Text GUI_GCORE_EDITOR_NPC_BEHAVIORCHECKLORE = new Text(
			"GUI_GCORE_EDITOR_NPC_BEHAVIORVARIABLECHECKLORE", file,
			"en_US", Utils.asList("&7What type of comparaison must be made"),
			"fr_FR", Utils.asList("&7Quel type de comparaison doit être fait")
			);

	public static final Text GUI_GCORE_EDITOR_NPC_BEHAVIORVARIABLEMULTIPLIERLORE = new Text(
			"GUI_GCORE_EDITOR_NPC_BEHAVIORVARIABLEMULTIPLIERLORE", file,
			"en_US", Utils.asList("&7The multiplier applied to the variable value"),
			"fr_FR", Utils.asList("&7Le multiplicateur appliqué à la valeur de la variable"),
			"es_ES", Utils.asList("&7El multiplicador aplicado al valor de la variable"),
			"zh_TW", Utils.asList("&7乘法應用於變量值")
			);

	public static final Text GUI_GCORE_EDITOR_NPC_BEHAVIORVARIABLEMODIFIERLORE = new Text(
			"GUI_GCORE_EDITOR_NPC_BEHAVIORVARIABLEMODIFIERLORE", file,
			"en_US", Utils.asList("&7The modifier added to the variable value"),
			"fr_FR", Utils.asList("&7Le modificateur ajouté à la valeur de la variable"),
			"es_ES", Utils.asList("&7El modificador añadido al valor de la variable"),
			"zh_TW", Utils.asList("&7新增到變量的變化")
			);

	public static final Text GUI_GCORE_EDITOR_NPC_BEHAVIORHEALTHMODIFIERLORE = new Text(
			"GUI_GCORE_EDITOR_NPC_BEHAVIORHEALTHMODIFIERLORE", file,
			"en_US", Utils.asList("&7The modifier added to the player life"),
			"fr_FR", Utils.asList("&7Le modificateur ajouté à la vie du joueur"),
			"es_ES", Utils.asList("&7El modificador agregado a la vida del jugador"),
			"zh_TW", Utils.asList("&7新增到玩家生命值上的變化")
			);

	public static final Text GUI_GCORE_EDITOR_NPC_BEHAVIORMESSAGESLORE = new Text(
			"GUI_GCORE_EDITOR_NPC_BEHAVIORMESSAGESLORE", file,
			"en_US", Utils.asList("&7The variants of the messages to be send"),
			"fr_FR", Utils.asList("&7Les variantes du message qui sera envoyé"),
			"es_ES", Utils.asList("&7Las variantes de este mensaje que se de deberán enviar"),
			"zh_TW", Utils.asList("&7顯示的變量訊息")
			);

	public static final Text GUI_GCORE_EDITOR_NPC_BEHAVIORMOVETARGETLORE = new Text(
			"GUI_GCORE_EDITOR_NPC_BEHAVIORMOVETARGETLORE", file,
			"en_US", Utils.asList("&7To where should the npc move (set a location or 'player')"),
			"fr_FR", Utils.asList("&7Où est-ce que le npc doit aller (définir une location ou 'player'"),
			"es_ES", Utils.asList("&7A donde se debería mover el NPC (defina una localización o 'player')"),
			"zh_TW", Utils.asList("&7NPC要往哪裡移動 (設定一個地點或事 '玩家')")
			);

	public static final Text GUI_GCORE_EDITOR_NPC_BEHAVIORANIMATIONLORE = new Text(
			"GUI_GCORE_EDITOR_NPC_BEHAVIORANIMATIONLORE", file,
			"en_US", Utils.asList("&7What animation should be played"),
			"fr_FR", Utils.asList("&7Quelle animation doit être jouée"),
			"es_ES", Utils.asList("&7¿Qué animación se debería activar?"),
			"zh_TW", Utils.asList("&7要執行哪個動畫")
			);

}

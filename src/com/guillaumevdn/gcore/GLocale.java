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
			"fr_FR", "&6{plugin} >> &7Vous n'avez pas la permission de faire cela.",
			"hu_HU", "&6{plugin} >> &7Nincs jogod ehhez.",
			"it_IT", Utils.asList("&6{plugin} >> &7Non hai il permesso di farlo.")
			);

	public static final Text MSG_GENERIC_NOHANDITEM = new Text(
			"MSG_GENERIC_NOHANDITEM", file,
			"en_US", "&6{plugin} >> &7You have to hold an item in your hand.",
			"fr_FR", "&6{plugin} >> &7Vous devez tenir un item dans votre main.",
			"hu_HU", "&6{plugin} >> &7Tartanod kell egy elemet a kezedben.",
			"it_IT", Utils.asList("&6{plugin} >> &7Devi tenere un oggetto in mano.")
			);

	public static final Text MSG_GENERIC_NOMONEY = new Text(
			"MSG_GENERIC_NOMONEY", file,
			"en_US", "&6{plugin} >> &7You need &c{money}$ &7but you only have &c{balance}$ &7in your bank account.",
			"fr_FR", "&6{plugin} >> &7Vous avez besoin de &c{money}$ &7mais vous avez seulement &c{balance}$ &7dans votre compte en banque.",
			"hu_HU", "&6{plugin} >> &7Neked kell &c{money}$ &7de csak van &c{balance}$ &7a bank fiókodban.",
			"it_IT", Utils.asList("&6{plugin} >> &7Hai bisogno di &c{money}$ &7ma hai solo &c{balance}$ &7a nel tuo account.")
			);

	public static final Text MSG_GENERIC_NAMETAKEN = new Text(
			"MSG_GENERIC_NAMETAKEN", file,
			"en_US", "&6{plugin} >> &7Name &c{name} &7is already taken.",
			"fr_FR", "&6{plugin} >> &7Le nom &c{name} &7est déjà utilisé.",
			"hu_HU", "&6{plugin} >> &7Név &c{name} &7már foglalt.",
			"it_IT", Utils.asList("&6{plugin} >> &7Il nome &c{name} &7è già in uso.")
			);

	public static final Text MSG_GENERIC_IDTAKEN = new Text(
			"MSG_GENERIC_IDTAKEN", file,
			"en_US", "&6{plugin} >> &7Id &c{id} &7is already taken.",
			"fr_FR", "&6{plugin} >> &7L'id &c{id} &7est déjà utilisé.",
			"hu_HU", "&6{plugin} >> &7Id &c{id} &7már foglalt.",
			"it_IT", Utils.asList("&6{plugin} >> &7L'Id &c{id} &7è già in uso.")
			);

	public static final Text MSG_GENERIC_INVALIDPLAYER = new Text(
			"MSG_GENERIC_INVALIDPLAYER", file,
			"en_US", "&6{plugin} >> &7Couldn't find online player &c{error}&7.",
			"fr_FR", "&6{plugin} >> &7Impossible de trouver le joueur connecté &c{error}&7.",
			"hu_HU", "&6{plugin} >> &7A játékos &c{player} &7nem található.",
			"it_IT", Utils.asList("&6{plugin} >> &7Non è possibile trovare questo giocatore online &c{error}.")
			);

	public static final Text MSG_GENERIC_INVALIDPLAYEROFFLINE = new Text(
			"MSG_GENERIC_INVALIDPLAYEROFFLINE", file,
			"en_US", "&6{plugin} >> &7Player &c{error} &7is invalid.",
			"fr_FR", "&6{plugin} >> &7Le joueur &c{error} &7est invalide.",
			"hu_HU", "&6{plugin} >> &7A játékos &c{error} &7érvénytelen.",
			"it_IT", Utils.asList("&6{plugin} >> &7Il giocatore &c{error} &7non è valido.")
			);

	public static final Text MSG_GENERIC_INVALIDDOUBLE = new Text(
			"MSG_GENERIC_INVALIDDOUBLE", file,
			"en_US", "&6{plugin} >> &7Decimal number &c{error} &7is invalid.",
			"fr_FR", "&6{plugin} >> &7Le nombre décimal &c{error} &7est invalide.",
			"hu_HU", "&6{plugin} >> &7Decimális szám &c{error} &7érvénytelen.",
			"it_IT", Utils.asList("&6{plugin} >> &7I numeri decimali &c{error} &7non sono validi.")
			);

	public static final Text MSG_GENERIC_INVALIDINT = new Text(
			"MSG_GENERIC_INVALIDINT", file,
			"en_US", "&6{plugin} >> &7Number &c{error} &7is invalid.",
			"fr_FR", "&6{plugin} >> &7Le nombre &c{error} &7est invalide.",
			"hu_HU", "&6{plugin} >> &7Szám &c{error} &7érvénytelen.",
			"it_IT", Utils.asList("&6{plugin} >> &7Il numero &c{error} &7non è valido.")
			);

	public static final Text MSG_GENERIC_INVALIDALPHANUMERIC = new Text(
			"MSG_GENERIC_INVALIDALPHANUMERIC", file,
			"en_US", "&6{plugin} >> &7Value &c{error} &7isn't alphanumeric.",
			"fr_FR", "&6{plugin} >> &7La valeur &c{error} &7n'est pas alphanumérique.",
			"hu_HU", "&6{plugin} >> &7Érték &c{error} &7nem alfanumerikus.",
			"it_IT", Utils.asList("&6{plugin} >> &7Il valore &c{error} &7non è alfanumerico.")
			);

	public static final Text MSG_GENERIC_NOTPLAYER = new Text(
			"MSG_GENERIC_NOTPLAYER", file,
			"en_US", "&6{plugin} >> &7You have to be in-game to do this.",
			"fr_FR", "&6{plugin} >> &7Vous devez être en jeu pour faire cela.",
			"hu_HU", "&6{plugin} >> &7Ehhez a játékban kell játszani.",
			"pl_PL", "&6{plugin} >> &7Musisz być w grze, aby to zrobić.",
			"it_IT", Utils.asList("&6{plugin} >> &7Devi essere in gioco per farlo.")
			);

	public static final Text MSG_GENERIC_INVALIDCROSSHAIRBLOCK = new Text(
			"MSG_GENERIC_INVALIDCROSSHAIRBLOCK", file,
			"en_US", "&6{plugin} >> &7You're not pointing any block.",
			"fr_FR", "&6{plugin} >> &7Vous ne pointez aucun bloc.",
			"hu_HU", "&6{plugin} >> &7Nem nézel egy blokkra sem.",
			"it_IT", Utils.asList("&6{plugin} >> &7Non stai indicando alcun blocco.")
			);

	public static final Text MSG_GENERIC_INVALIDACTION = new Text(
			"MSG_GENERIC_INVALIDACTION", file,
			"en_US", "&6{plugin} >> &7This action is invalid or has expired.",
			"fr_FR", "&6{plugin} >> &7L'action est invalide ou a expiré.",
			"hu_HU", "&6{plugin} >> &7Ez a művelet érvénytelen vagy lejárt.",
			"it_IT", Utils.asList("&6{plugin} >> &7Questa azione non è valida o è scaduta.")
			);

	public static final Text MSG_GENERIC_RELOAD = new Text(
			"MSG_GENERIC_RELOAD", file,
			"en_US", "&6{plugin} >> &7Plugin was reloaded (took {time}).",
			"fr_FR", "&6{plugin} >> &7Le plugin a été reload (took {time}).",
			"hu_HU", "&6{plugin} >> &7A plugin újratöltve (took {time}).",
			"it_IT", Utils.asList("&6{plugin} >> &7Il plugin è stato ricaricato (took {time}).")
			);

	public static final Text MSG_GENERIC_NOTHING = new Text(
			"MSG_GENERIC_NOTHING", file,
			"en_US", "&6{plugin} >> &7There's nothing to see here.",
			"fr_FR", "&6{plugin} >> &7Il n'y a rien à voir ici.",
			"hu_HU", "&6{plugin} >> &7Itt nincs mit látni.",
			"pl_PL", "&6{plugin} >> &7Tu nic nie ma.",
			"it_IT", Utils.asList("&6{plugin} >> &7Non c'è niente da vedere qui.")
			);

	public static final Text MSG_GENERIC_COOLDOWN = new Text(
			"MSG_GENERIC_COOLDOWN", file,
			"en_US", "&6{plugin} >> &7You have to wait &c{time} &7to do this again.",
			"fr_FR", "&6{plugin} >> &7Vous devez attendre &c{time} &7pour faire cela à nouveau.",
			"hu_HU", "&6{plugin} >> &7Meg kell várni &c{time}&7, hogy ezt újra elvégezhesse.",
			"it_IT", Utils.asList("&6{plugin} >> &7Devi aspettare &c{time}&7, per rifarlo.")
			);

	public static final Text MSG_GENERIC_COMMAND_DISABLED = new Text(
			"MSG_GENERIC_COMMAND_DISABLED", file,
			"en_US", "&6{plugin} >> &7This command is disabled.",
			"es_ES", "&6{plugin} >> &7Comando desactivado.",
			"fr_FR", "&6{plugin} >> &7Cette commande est désactivée.",
			"hu_HU", "&6{plugin} >> &7Ez a parancs letiltva.",
			"pl_PL", "&6{plugin} >> &7To polecenie jest wyłączone.",
			"ru_RU", "&6{plugin} >> &7Эта команда отключена.",
			"sv_FI", "&6{plugin} >> &7Tämä komento ei ole käytössä.",
			"it_IT", Utils.asList("&6{plugin} >> &7Questo comando è disabilitato.")
			);

	public static final Text MSG_GENERIC_COMMAND_NOCHILDREN = new Text(
			"MSG_GENERIC_COMMAND_NOCHILDREN", file,
			"en_US", "&6{plugin} >> &7You specified too many arguments, it should end after &c{current_path}&7. Use &c{current_path} -help &7or &c-help:page &7and refer to tab completion to show help.",
			"fr_FR", "&6{plugin} >> &7Vous avez spécifié trop d'arguments, cela devrait s'arrêter après &c{current_path}&7. Utilisez &c{current_path} -help &7ou &c-help:page &7and refer to tab completion to show help.",
			"hu_HU", "&6{plugin} >> &7Túl sok argumentumot adtál meg, a &c{current_path}&7. Use &c{current_path} -help &7or &c-help:page &7and refer to tab completion to show help.",
			"it_IT", Utils.asList("&6{plugin} >> &7Hai inserito troppi argomenti, potrebbe finire dopo &c{current_path}&7.")
			);

	public static final Text MSG_GENERIC_COMMAND_NOCHILDPERFORMED = new Text(
			"MSG_GENERIC_COMMAND_NOCHILDPERFORMED", file,
			"en_US", "&6{plugin} >> &7Couldn't understand the arguments you specified after &c{current_path}&7.",
			"fr_FR", "&6{plugin} >> &7Impossible de comprendre les arguments que vous avez spécifié après &c{current_path}&7.",
			"hu_HU", "&6{plugin} >> &7Nem sikerült megérteni a &c{current_path}&7 után megadott argumentumokat&7.",
			"it_IT", Utils.asList("&6{plugin} >> &7Non è possibile capire gli argomenti specificati dopo &c{current_path}&7..")
			);

	public static final Text MSG_GENERIC_COMMAND_MISSINGPARAM = new Text(
			"MSG_GENERIC_COMMAND_MISSINGPARAM", file,
			"en_US", "&6{plugin} >> &7You're missing parameter &c{parameter}&7.",
			"fr_FR", "&6{plugin} >> &7Il manque le paramètre &c{parameter}&7.",
			"hu_HU", "&6{plugin} >> &7A paraméter hiányzik &c{parameter}&7.",
			"it_IT", Utils.asList("&6{plugin} >> &7Hai mancato il parametro &c{parameter}&7.")
			);

	public static final Text MSG_GENERIC_COMMAND_INVALIDALPHANUMERICPARAM = new Text(
			"MSG_GENERIC_COMMAND_INVALIDALPHANUMERICPARAM", file,
			"en_US", "&6{plugin} >> &7Parameter &c{parameter} &7should be alphanumeric.",
			"fr_FR", "&6{plugin} >> &7Le paramètre &c{parameter} &7devrait être alphanumérique.",
			"hu_HU", "&6{plugin} >> &7A paraméter &c{parameter} &7alfanumerikusnak kell lennie.",
			"it_IT", Utils.asList("&6{plugin} >> &7Il parametro &c{parameter} &7deve essere alfanumerico.")
			);

	public static final Text MSG_GENERIC_COMMAND_INVALIDINTPARAM = new Text(
			"MSG_GENERIC_COMMAND_INVALIDINTPARAM", file,
			"en_US", "&6{plugin} >> &7Parameter &c{parameter} &7should be a number.",
			"fr_FR", "&6{plugin} >> &7Le paramètre &c{parameter} &7devrait être un nombre entier.",
			"hu_HU", "&6{plugin} >> &7A paraméter &c{parameter} &7számnak kell lennie.",
			"it_IT", Utils.asList("&6{plugin} >> &7Il parametro &c{parameter} &7dovrebbe essere un numero.")
			);

	public static final Text MSG_GENERIC_COMMAND_INVALIDDOUBLEPARAM = new Text(
			"MSG_GENERIC_COMMAND_INVALIDDOUBLEPARAM", file,
			"en_US", "&6{plugin} >> &7Parameter &c{parameter} &7should be a decimal number.",
			"fr_FR", "&6{plugin} >> &7Le paramètre &c{parameter} &7devrait être un nombre décimal.",
			"hu_HU", "&6{plugin} >> &7A paraméter &c{parameter} &7tizedes számnak kell lennie.",
			"it_IT", Utils.asList("&6{plugin} >> &7Il parametro &c{parameter} &7dovrebbe essere un numero decimale.")
			);

	public static final Text MSG_GENERIC_COMMAND_INVALIDENUMPARAM = new Text(
			"MSG_GENERIC_COMMAND_INVALIDENUMPARAM", file,
			"en_US", "&6{plugin} >> &7Parameter &c{parameter} &7should be a valid &c{enum}&7.",
			"fr_FR", "&6{plugin} >> &7Le paramètre &c{parameter} &7devrait être un &c{enum} &7valide.",
			"hu_HU", "&6{plugin} >> &7A paraméter &c{parameter} &7érvényesnek kell lennie &c{enum}&7.",
			"it_IT", Utils.asList("&6{plugin} >> &7Il parametro &c{parameter} &7deve essere un numero valido &c{enum}&7.")
			);

	public static final Text MSG_GENERIC_COMMAND_INVALIDOFFLINEPLAYERPARAM = new Text(
			"MSG_GENERIC_COMMAND_INVALIDOFFLINEPLAYERPARAM", file,
			"en_US", "&6{plugin} >> &7Parameter &c{parameter} &7should be an existing player.",
			"fr_FR", "&6{plugin} >> &7Le paramètre &c{parameter} &7devrait être un joueur existant.",
			"hu_HU", "&6{plugin} >> &7A paraméter &c{parameter} &7létező játékosnak kell lennie.",
			"it_IT", Utils.asList("&6{plugin} >> &7Il parametro &c{parameter} &7dovrebbe essere un giocatore esistente.")
			);

	public static final Text MSG_GENERIC_COMMAND_INVALIDPLAYERPARAM = new Text(
			"MSG_GENERIC_COMMAND_INVALIDPLAYERPARAM", file,
			"en_US", "&6{plugin} >> &7Parameter &c{parameter} &7should be an online player.",
			"fr_FR", "&6{plugin} >> &7Le paramètre &c{parameter} &7devrait être un joueur connecté.",
			"hu_HU", "&6{plugin} >> &7A paraméter &c{parameter} &7elérhető játékosnak kell lennie.",
			"it_IT", Utils.asList("&6{plugin} >> &7Il parametro &c{parameter} &7dovrebbe essere un giocatore online.")
			);

	public static final Text MSG_GENERIC_COMMAND_INVALIDUUIDPARAM = new Text(
			"MSG_GENERIC_COMMAND_INVALIDUUIDPARAM", file,
			"en_US", "&6{plugin} >> &7Parameter &c{parameter} &7should be an UUID.",
			"fr_FR", "&6{plugin} >> &7Le paramètre &c{parameter} &7devrait être un UUID.",
			"hu_HU", "&6{plugin} >> &7A paraméter &c{parameter} &7UUD-nek kell lennie.",
			"it_IT", Utils.asList("&6{plugin} >> &7Il parametro &c{parameter} &7dovrebbe essere un UUID.")
			);

	// gcore messages
	public static final Text MSG_GCORE_INVALIDPLUGINPARAM = new Text(
			"MSG_GCORE_INVALIDPLUGINPARAM", file,
			"en_US", "&6{plugin} >> &7Parameter &c{parameter} &7should be a plugin registered with GCore.",
			"fr_FR", "&6{plugin} >> &7Le paramètre &c{parameter} &7devrait être un plugin enregistré avec GCore.",
			"hu_HU", "&6{plugin} >> &7A paraméter &c{parameter} &7a GCore-ban regisztrált bővítménynek kell lennie.",
			"it_IT", Utils.asList("&6{plugin} >> &7Il parametro &c{parameter} &7a dovrebbe essere un plugin registrato con GCore.")
			);

	public static final Text MSG_GCORE_PLUGINSLIST = new Text(
			"MSG_GCORE_PLUGINSLIST", file,
			"en_US", Utils.asList("&6{plugin} >> &7There are &a{count} plugin{plural} &7registered :", "&a{plugins}"),
			"fr_FR", Utils.asList("&6{plugin} >> &7Il y a &a{count} plugin{plural} &7enregistrés :", "&a{plugins}"),
			"hu_HU", Utils.asList("&6{plugin} >> &7Van &a{count} plugin{plural} &7regisztrálva :", "&a{plugins}"),
			"it_IT", Utils.asList("&6{plugin} >> &7Ci sono &a{count} plugin{plural} &7registrati :", "&a{plugins}")
			);

	// ------------------------------------------------------------
	// MISC
	// ------------------------------------------------------------

	// generic
	public static final Text MISC_GENERIC_TIMEFORMATSECONDS = new Text(
			"MISC_GENERIC_TIMEFORMATSECONDS", file,
			"en_US", "{seconds}s",
			"hu_HU", "{seconds}m",
			"it_IT", Utils.asList("{seconds}s")
			);

	public static final Text MISC_GENERIC_TIMEFORMATMINUTES = new Text(
			"MISC_GENERIC_TIMEFORMATMINUTES", file,
			"en_US", "{minutes}m {seconds}s",
			"hu_HU", "{minutes}p {seconds}m",
			"it_IT", Utils.asList("{minutes}m {seconds}m")
			);

	public static final Text MISC_GENERIC_TIMEFORMATHOURS = new Text(
			"MISC_GENERIC_TIMEFORMATHOURS", file,
			"en_US", "{hours}h {minutes}m {seconds}s",
			"hu_HU", "{hours}ó {minutes}p {seconds}m",
			"it_IT", Utils.asList("{hours}h {minutes}m {seconds}s")
			);

	public static final Text MISC_GENERIC_TIMEFORMATDAYS = new Text(
			"MISC_GENERIC_TIMEFORMATDAYS", file,
			"en_US", "{days}d {hours}h {minutes}m {seconds}s",
			"hu_HU", "{days}n {hours}ó {minutes}p {seconds}m",
			"it_IT", Utils.asList("{days}d {hours}h {minutes}m {seconds}s")
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
			"it_IT", Utils.asList("&6Pagina precedente")
			);

	public static final Text GUI_GENERIC_NEXTPAGEITEM = new Text(
			"GUI_GENERIC_NEXTPAGEITEM", file,
			"en_US", "&6Next page",
			"fr_FR", "&6Page suivante",
			"hu_HU", "&6Következő oldal",
			"it_IT", Utils.asList("&6Pagina successiva")
			);

	public static final Text GUI_GENERIC_CONFIRMNAME = new Text(
			"GUI_GENERIC_CONFIRMNAME", file,
			"en_US", "Confirm action",
			"fr_FR", "Confirmer action",
			"hu_HU", "Megerősít akció",
			"it_IT", Utils.asList("Conferma azione")
			);

	// ------------------------------------------------------------
	// Editor
	// ------------------------------------------------------------

	// editor : messages
	public static final Text MSG_GENERIC_DUPLICATEELEMENT = new Text(
			"MSG_GENERIC_DUPLICATEELEMENT", file,
			"en_US", "&7An element with id &6{id} &7already exists.",
			"fr_FR", "&7Un élément avec l'id &6{id} &7existe déjà.",
			"it_IT", Utils.asList("&7Un elemento con l'id &6{id} &7esiste già.")
			);

	public static final Text MSG_GENERIC_CHATINPUT = new Text(
			"MSG_GENERIC_CHATINPUT", file,
			"en_US", "&7Type the new value or &ccancel &7to cancel.",
			"fr_FR", "&7Entrez une nouvelle valeur ou bien &ccancel &7pour annuler.",
			"it_IT", Utils.asList("&7Digita un nuovo valore o &ccancel &7per annullare.")
			);

	public static final Text MSG_GENERIC_CHATINPUTID = new Text(
			"MSG_GENERIC_CHATINPUTID", file,
			"en_US", "&7Enter an &aID &7in the chat or &ccancel &7to cancel.",
			"fr_FR", "&7Entrez un &aID &7dans le chat ou &ccancel &7pour annuler.",
			"it_IT", Utils.asList("&7Inserisci un &aID &7in chat o &ccancel &7per annullare.")
			);

	public static final Text MSG_GENERIC_LOCATIONINPUT = new Text(
			"MSG_GENERIC_LOCATIONINPUT", file,
			"en_US", "&7Press 'sneak' when you're ready to import your location, or right-click a block.",
			"fr_FR", "&7Appuyez sur 's'accroupir' lorsque vous serez prêt à importer votre location, ou bien cliquez sur un bloc.",
			"it_IT", Utils.asList("&7Premere 'sneak' quando sei pronto a importare la tua posizione, o tasto destro su un blocco.")
			);

	public static final Text MSG_GENERIC_ITEMINPUT = new Text(
			"MSG_GENERIC_ITEMINPUT", file,
			"en_US", "&7Press 'drop' when you're ready to import the item in your hand.",
			"fr_FR", "&7Appuyez sur 'jeter l'objet' lorsque vous serez prêt à importer l'item dans votre main.",
			"it_IT", Utils.asList("&7Premere 'drop' quando sei pronto a importare gli oggetti nella tua mano.")
			);

	public static final Text MSG_GENERIC_DELETEELEMENT = new Text(
			"MSG_GENERIC_DELETEELEMENT", file,
			"en_US", "&7Click on the element that you wish to delete (cancel by closing the GUI).",
			"fr_FR", "&7Cliquez sur l'élement que vous souhaitez supprimer (annulez en fermant le GUI)."
			);

	// items
	public static final Text GUI_GENERIC_EDITORITEMDELETESELF = new Text(
			"GUI_GENERIC_EDITORITEMDELETESELF", file,
			"en_US", "&6Reset",
			"fr_FR", "&6Réinitialiser",
			"it_IT", Utils.asList("&6Resetta")
			);

	public static final Text GUI_GENERIC_EDITORITEMDELETESELFLORE = new Text(
			"GUI_GENERIC_EDITORITEMDELETESELFLORE", file,
			"en_US", Utils.asList("&c&lThis action is irreversible", "&cClick this icon to reset every", "&c setting of this element"),
			"fr_FR", Utils.asList("&c&lCette action est irréversible", "&cCliquez sur cette icône pour", "&c réinitialiser chaque paramètre de", "&c cet élément"),
			"it_IT", Utils.asList("&c&lQuesta azione è irreversibile", "&cClicca questa icona per resettare", "&ctutte le impostazioni di questo", "&celemento")
			);

	public static final Text GUI_GENERIC_EDITORITEMBACK = new Text(
			"GUI_GENERIC_EDITORITEMBACK", file,
			"en_US", "&7Back",
			"fr_FR", "&7Retour",
			"it_IT", Utils.asList("&7Indietro")
			);

	public static final Text GUI_GENERIC_EDITORITEMADD = new Text(
			"GUI_GENERIC_EDITORITEMADD", file,
			"en_US", "&6Add",
			"fr_FR", "&6Ajouter",
			"it_IT", Utils.asList("&6Aggiungi")
			);

	public static final Text GUI_GENERIC_EDITORITEMADDNUMBER = new Text(
			"GUI_GENERIC_EDITORITEMADDNUMBER", file,
			"en_US", "&6Add number",
			"fr_FR", "&6Ajouter un nombre"
			);

	public static final Text GUI_GENERIC_EDITORITEMADDENTITYNAMED = new Text(
			"GUI_GENERIC_EDITORITEMADDENTITYNAMED", file,
			"en_US", "&6Add named entity",
			"fr_FR", "&6Ajouter une entité nommée"
			);

	public static final Text GUI_GENERIC_EDITORITEMDELETE = new Text(
			"GUI_GENERIC_EDITORITEMDELETE", file,
			"en_US", "&6Delete",
			"fr_FR", "&6Supprimer",
			"it_IT", Utils.asList("&6Elimina")
			);

	public static final Text GUI_GENERIC_EDITORITEMDELETELORE = new Text(
			"GUI_GENERIC_EDITORITEMDELETELORE", file,
			"en_US", Utils.asList("&c&lThis action is irreversible", "&cClick this icon, then the element", "&c that you wish to delete"),
			"fr_FR", Utils.asList("&c&lCette action est irréversible", "&cCliquez sur cette icône, ensuite sur", "&c l'élement que vous souhaitez supprimer"),
			"it_IT", Utils.asList("&c&lQuesta azione è irreversibile", "&cClicca su questa icona, poi sull'elemento", "&c che desideri eliminare")
			);

	public static final Text GUI_GENERIC_EDITORCURRENTLORE = new Text(
			"GUI_GENERIC_EDITORCURRENTLORE", file,
			"en_US", Utils.asList("&7{description}", "", "&5Mandatory value : &d{mandatory}", "&5Value type : &d{type}", "", "&5Current value :", "&e{current}"),
			"fr_FR", Utils.asList("&7{description}", "", "", "&5Valeur obligatoire : &d{mandatory}", "&5Type de valeur : &d{type}", "", "&5Valeur actuelle :", "&e{current}")
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
			"it_IT", Utils.asList("&6Valore originale")
			);

	public static final Text GUI_GENERIC_EDITORRAWLORE = new Text(
			"GUI_GENERIC_EDITORRAWLORE", file,
			"en_US", Utils.asList("&7Edit the raw value (it will be parsed", "&7 for a player when needed)", "", "{placeholders}"),
			"fr_FR", Utils.asList("&7Éditer la valeur brute (elle sera", "&7 convertie pour un joueur quand nécessaire)", "", "{placeholders}"),
			"it_IT", Utils.asList("&7Modifica il valore originale (sarà analizzato", "&7per un giocatore quando sarà necessario)", "{placeholders}")
			);

	public static final Text GUI_GENERIC_EDITORTYPERAW = new Text(
			"GUI_GENERIC_EDITORTYPERAW", file,
			"en_US", "&6Raw type value",
			"fr_FR", "&6Valeur brute de type",
			"it_IT", Utils.asList("&6Valore originale non trovato")
			);

	public static final Text GUI_GENERIC_EDITORTEXTLINELORE = new Text(
			"GUI_GENERIC_EDITORTEXTLINELORE", file,
			"en_US", Utils.asList("&7Edit the raw value (it will be parsed", "&7 for a player when needed)", "{placeholders}", "&7Change colors with &{code}", "", "&a&lLeft-click to edit", "&a&lRight-click to delete"),
			"fr_FR", Utils.asList("&7Éditer la valeur brute (elle sera convertie pour un joueur quand nécessaire)", "{placeholders}", "&7Change colors with &{code}", "", "&a&lClic gauche pour éditer", "&a&lClic droit pour supprimer"),
			"it_IT", Utils.asList("&7Modifica il valore originale (sarà analizzato ", "&7per un giocatore quando sarà necessario)", "{placeholders}", "&7Cambia il colore con &{code}", "", "&a&lTasto sinistro per modificare", "&a&lTasto destro per eliminare", "", "&a&l", "", "&5Valore obbligatorio: &d{value_mandatory}", "&5Tipo di valore: &d{value_type}", "", "&5Valore attuale:", "&d{value_current")
			);

	public static final Text GUI_GENERIC_EDITORLISTELEMENTLORE = new Text(
			"GUI_GENERIC_EDITORLISTELEMENTLORE", file,
			"en_US", Utils.asList("&a&lLeft-click to edit", "&a&lRight-click to delete"),
			"fr_FR", Utils.asList("&a&lClic gauche pour éditer", "&a&lClic droit pour supprimer")
			);

	public static final Text GUI_GENERIC_EDITORBOOLEANTOGGLE = new Text(
			"GUI_GENERIC_EDITORBOOLEANTOGGLE", file,
			"en_US", "&6Toggle",
			"fr_FR", "&6Basculer",
			"it_IT", Utils.asList("&6Disabilita")
			);

	public static final Text GUI_GENERIC_EDITORBOOLEANTOGGLELORE = new Text(
			"GUI_GENERIC_EDITORBOOLEANTOGGLELORE", file,
			"en_US", Utils.asList("&7Set the value to true/false"),
			"fr_FR", Utils.asList("&7Définir la valeur sur vrai/faux"),
			"it_IT", Utils.asList("&7Imposta il valore su vero/falso")
			);

	public static final Text GUI_GENERIC_EDITORNUMBERADD = new Text(
			"GUI_GENERIC_EDITORNUMBERADD", file,
			"en_US", "&a+{amount}"
			);

	public static final Text GUI_GENERIC_EDITORNUMBERTAKE = new Text(
			"GUI_GENERIC_EDITORNUMBERTAKE", file,
			"en_US", "&c-{amount}"
			);

	public static final Text GUI_GENERIC_EDITORNUMBERADDTAKELORE = new Text(
			"GUI_GENERIC_EDITORNUMBERADDTAKELORE", file,
			"en_US", Utils.asList("&7Click to apply"),
			"fr_FR", Utils.asList("&7Cliquez pour appliquer")
			);

	public static final Text GUI_GENERIC_EDITORLOCATIONIMPORT = new Text(
			"GUI_GENERIC_EDITORLOCATIONIMPORT", file,
			"en_US", "&6Select",
			"fr_FR", "&6Sélectionner",
			"it_IT", Utils.asList("&6Seleziona")
			);

	public static final Text GUI_GENERIC_EDITORLOCATIONIMPORTLORE = new Text(
			"GUI_GENERIC_EDITORLOCATIONIMPORTLORE", file,
			"en_US", Utils.asList("&7Import a value (place or block)"),
			"fr_FR", Utils.asList("&7Importer une valeur (endroit ou bloc)"),
			"it_IT", Utils.asList("&7Importa un valore (place o blocco)")
			);

	public static final Text GUI_GENERIC_EDITORITEMIMPORT = new Text(
			"GUI_GENERIC_EDITORITEMIMPORT", file,
			"en_US", "&6Select",
			"fr_FR", "&6Sélectionner",
			"it_IT", Utils.asList("&6Seleziona")
			);

	public static final Text GUI_GENERIC_EDITORITEMIMPORTLORE = new Text(
			"GUI_GENERIC_EDITORITEMIMPORTLORE", file,
			"en_US", Utils.asList("&7Import a value (item)"),
			"fr_FR", Utils.asList("&7Importer une valeur (item)"),
			"it_IT", Utils.asList("&7Importa un valore (item)")
			);

	public static final Text GUI_GENERIC_EDITORENUMSELECT = new Text(
			"GUI_GENERIC_EDITORENUMSELECT", file,
			"en_US", "&6Select",
			"fr_FR", "&6Sélectionner",
			"it_IT", Utils.asList("&6Seleziona")
			);

	public static final Text GUI_GENERIC_EDITORENUMSELECTLORE = new Text(
			"GUI_GENERIC_EDITORENUMSELECTLORE", file,
			"en_US", Utils.asList("&7Select a regular value"),
			"fr_FR", Utils.asList("&7Sélectionner une valeur classique"),
			"it_IT", Utils.asList("&7Seleziona un valore regolare")
			);

	public static final Text GUI_GENERIC_EDITORENUMTYPESELECT = new Text(
			"GUI_GENERIC_EDITORENUMTYPESELECT", file,
			"en_US", "&6Select type",
			"fr_FR", "&6Sélectionner le type",
			"it_IT", Utils.asList("&6Seleziona il tipo")
			);

	// editor : block setting
	public static final Text GUI_GENERIC_EDITOR_BLOCKTYPELORE = new Text(
			"GUI_GENERIC_EDITOR_BLOCKTYPELORE", file,
			"en_US", Utils.asList("&7Type of block"),
			"fr_FR", Utils.asList("&7Type de bloc"),
			"it_IT", Utils.asList("&7Tipo di blocco")
			);

	// editor : mob setting
	public static final Text GUI_GENERIC_EDITOR_MOB_TYPELORE = new Text(
			"GUI_GENERIC_EDITOR_MOB_TYPELORE", file,
			"en_US", Utils.asList("&7Type of mob"),
			"fr_FR", Utils.asList("&7Type de mob"),
			"it_IT", Utils.asList("&7Tipo di mob")
			);

	public static final Text GUI_GENERIC_EDITOR_MOB_NAMELORE = new Text(
			"GUI_GENERIC_EDITOR_MOB_NAMELORE", file,
			"en_US", Utils.asList("&7Name of mob"),
			"fr_FR", Utils.asList("&7Nom de mob"),
			"it_IT", Utils.asList("&7Nome del mob")
			);

	public static final Text GUI_GENERIC_EDITOR_MOB_AMOUNTLORE = new Text(
			"GUI_GENERIC_EDITOR_MOB_AMOUNTLORE", file,
			"en_US", Utils.asList("&7Mob amount"),
			"fr_FR", Utils.asList("&7Nombre de mobs")
			);

	// editor : potion effect setting
	public static final Text GUI_GENERIC_EDITOR_POTIONEFFECT_TYPELORE = new Text(
			"GUI_GENERIC_EDITOR_POTIONEFFECT_TYPELORE", file,
			"en_US", Utils.asList("&7Type of effect"),
			"fr_FR", Utils.asList("&7Type d'effet"),
			"it_IT", Utils.asList("&7Tipo d'effetto")
			);

	public static final Text GUI_GENERIC_EDITOR_POTIONEFFECT_LEVELLORE = new Text(
			"GUI_GENERIC_EDITOR_POTIONEFFECT_LEVELLORE", file,
			"en_US", Utils.asList("&7Level of effect (starting at 1)"),
			"fr_FR", Utils.asList("&7Niveau de l'effet (commançant à 1)"),
			"it_IT", Utils.asList("&7Livello dell'effetto (comincia da 1)")
			);

	public static final Text GUI_GENERIC_EDITOR_POTIONEFFECT_DURATIONLORE = new Text(
			"GUI_GENERIC_EDITOR_POTIONEFFECT_DURATIONLORE", file,
			"en_US", Utils.asList("&7Duration of effect (in ticks, 1 sec = 20 ticks)"),
			"fr_FR", Utils.asList("&7Durée de l'effet (en ticks, 1 sec = 20 ticks)"),
			"it_IT", Utils.asList("&7Durata dell'effetto (in tick, 1 sec = 20 ticks)")
			);

	// editor : npc
	public static final Text GUI_GENERIC_EDITOR_NPC_SHOWLORE = new Text(
			"GUI_GENERIC_EDITOR_NPC_SHOWLORE", file,
			"en_US", Utils.asList("&7Should the NPC be shown by default"),
			"fr_FR", Utils.asList("&7Est-ce que le NPC doit être montré par défaut")
			);

	public static final Text GUI_GENERIC_EDITOR_NPC_NAMELORE = new Text(
			"GUI_GENERIC_EDITOR_NPC_NAMELORE", file,
			"en_US", Utils.asList("&7Name of the NPC"),
			"fr_FR", Utils.asList("&7Nom du NPC")
			);

	public static final Text GUI_GENERIC_EDITOR_NPC_SKINLORE = new Text(
			"GUI_GENERIC_EDITOR_NPC_SKINLORE", file,
			"en_US", Utils.asList("&7UUID of a player from whom the skin", "&7 will be used by this NPC"),
			"fr_FR", Utils.asList("&7UUID d'un joueur dont le skin sera", "&7 utilisé par ce NPC")
			);

	public static final Text GUI_GENERIC_EDITOR_NPC_LOCATIONLORE = new Text(
			"GUI_GENERIC_EDITOR_NPC_LOCATIONLORE", file,
			"en_US", Utils.asList("&7Default location of the NPC"),
			"fr_FR", Utils.asList("&7Emplacement par défaut du NPC")
			);

	public static final Text GUI_GENERIC_EDITOR_NPC_TARGEDISTANCELORE = new Text(
			"GUI_GENERIC_EDITOR_NPC_TARGEDISTANCELORE", file,
			"en_US", Utils.asList("&7Distance under which the NPC will", "&7 look at the player when close"),
			"fr_FR", Utils.asList("&7Distance à laquelle le NPC regardera", "&7 le joueur s'il en est proche")
			);

	public static final Text GUI_GENERIC_EDITOR_NPC_STATUSLORE = new Text(
			"GUI_GENERIC_EDITOR_NPC_STATUSLORE", file,
			"en_US", Utils.asList("&7List of status of the NPC"),
			"fr_FR", Utils.asList("&7Liste de status du NPC")
			);

	public static final Text GUI_GENERIC_EDITOR_NPC_STUFFLORE = new Text(
			"GUI_GENERIC_EDITOR_NPC_STUFFLORE", file,
			"en_US", Utils.asList("&7Stuff by default of the NPC"),
			"fr_FR", Utils.asList("&7Équipement par défaut du NPC")
			);

	// editor : sound setting
	public static final Text GUI_GENERIC_EDITOR_SOUND_TYPELORE = new Text(
			"GUI_GENERIC_EDITOR_SOUND_TYPELORE", file,
			"en_US", Utils.asList("&7Type of sound"),
			"fr_FR", Utils.asList("&7Type de son"),
			"it_IT", Utils.asList("&7Tipo di suono")
			);

	public static final Text GUI_GENERIC_EDITOR_SOUND_VOLUMELORE = new Text(
			"GUI_GENERIC_EDITOR_SOUND_VOLUMELORE", file,
			"en_US", Utils.asList("&7Volume of sound (normal = 1.0)"),
			"fr_FR", Utils.asList("&7Volume du son (normal = 1.0)"),
			"it_IT", Utils.asList("&7Volume del suono (normale = 1.0)")
			);

	public static final Text GUI_GENERIC_EDITOR_SOUND_PITCH = new Text(
			"GUI_GENERIC_EDITOR_SOUND_PITCH", file,
			"en_US", Utils.asList("&7Pitch of sound (normal = 1.0)"),
			"fr_FR", Utils.asList("&7Pitch du son (normal = 1.0)"),
			"it_IT", Utils.asList("&7Passo del suono (normale = 1.0)")
			);

	// editor : tab setting
	public static final Text GUI_GENERIC_EDITOR_TAB_HEADERLORE = new Text(
			"GUI_GENERIC_EDITOR_TAB_HEADERLORE", file,
			"en_US", Utils.asList("&7Header"),
			"fr_FR", Utils.asList("&7En-tête"),
			"it_IT", Utils.asList("&7Intestazione")
			);

	public static final Text GUI_GENERIC_EDITOR_TAB_FOOTERLORE = new Text(
			"GUI_GENERIC_EDITOR_TAB_FOOTERLORE", file,
			"en_US", Utils.asList("&7Footer"),
			"fr_FR", Utils.asList("&7Bas de page"),
			"it_IT", Utils.asList("&7Fondo della pagina")
			);

	// editor : title setting
	public static final Text GUI_GENERIC_EDITOR_TITLE_TITLELORE = new Text(
			"GUI_GENERIC_EDITOR_TITLE_TITLELORE", file,
			"en_US", Utils.asList("&7Title"),
			"fr_FR", Utils.asList("&7Titre"),
			"it_IT", Utils.asList("&7Titolo")
			);

	public static final Text GUI_GENERIC_EDITOR_TITLE_SUBTITLELORE = new Text(
			"GUI_GENERIC_EDITOR_TITLE_SUBTITLELORE", file,
			"en_US", Utils.asList("&7Subtitle"),
			"fr_FR", Utils.asList("&7Sous-titre"),
			"it_IT", Utils.asList("&7Sottotitolo")
			);

	public static final Text GUI_GENERIC_EDITOR_TITLE_FADEINLORE = new Text(
			"GUI_GENERIC_EDITOR_TITLE_FADEINLORE", file,
			"en_US", Utils.asList("&7Fade in (in ticks) (1 sec = 20 ticks)"),
			"fr_FR", Utils.asList("&7Temps d'apparition (en ticks) (1 sec = 20 ticks)"),
			"it_IT", Utils.asList("&7Tempo d'attesa (in tick) (1 sec = 20 ticks)")
			);

	public static final Text GUI_GENERIC_EDITOR_TITLE_DURATIONLORE = new Text(
			"GUI_GENERIC_EDITOR_TITLE_DURATIONLORE", file,
			"en_US", Utils.asList("&7Duration (in ticks) (1 sec = 20 ticks)"),
			"fr_FR", Utils.asList("&7Durée (en ticks) (1 sec = 20 ticks)"),
			"it_IT", Utils.asList("&7Durata (in ticks) (1 sec = 20 ticks)")
			);

	public static final Text GUI_GENERIC_EDITOR_TITLE_FADEOUTLORE = new Text(
			"GUI_GENERIC_EDITOR_TITLE_FADEOUTLORE", file,
			"en_US", Utils.asList("&7Fade out (in ticks) (1 sec = 20 ticks)"),
			"fr_FR", Utils.asList("&7Temps de disparition (en ticks) (1 sec = 20 ticks)"),
			"it_IT", Utils.asList("&7Tempo di scomparsa (in ticks) (1 sec = 20 ticks)")
			);

	// editor : item setting
	public static final Text GUI_GENERIC_EDITOR_ITEM_SLOTLORE = new Text(
			"GUI_GENERIC_EDITOR_ITEM_SLOTLORE", file,
			"en_US", Utils.asList("&7Slot in the GUI"),
			"fr_FR", Utils.asList("&7Position dans le GUI"),
			"it_IT", Utils.asList("&7Posizione nella GUI")
			);

	public static final Text GUI_GENERIC_EDITOR_ITEM_CHANCELORE = new Text(
			"GUI_GENERIC_EDITOR_ITEM_CHANCELORE", file,
			"en_US", Utils.asList("&7Item apparition chance"),
			"fr_FR", Utils.asList("&7Chance pour l'item d'apparaître"),
			"it_IT", Utils.asList("&7Cambia il tempo d'attesa")
			);

	public static final Text GUI_GENERIC_EDITOR_ITEM_MAXAMOUNTLORE = new Text(
			"GUI_GENERIC_EDITOR_ITEM_MAXAMOUNTLORE", file,
			"en_US", Utils.asList("&7Maximum items amount"),
			"fr_FR", Utils.asList("&7Nombre d'items maximum"),
			"it_IT", Utils.asList("&7Numero massimo di item")
			);

	public static final Text GUI_GENERIC_EDITOR_ITEM_ENABLEDLORE = new Text(
			"GUI_GENERIC_EDITOR_ITEM_ENABLEDLORE", file,
			"en_US", Utils.asList("&7Is the item enabled"),
			"fr_FR", Utils.asList("&7Est-ce que l'item est activé"),
			"it_IT", Utils.asList("&7È l'oggetto attivo")
			);

	public static final Text GUI_GENERIC_EDITOR_ITEM_TYPELORE = new Text(
			"GUI_GENERIC_EDITOR_ITEM_TYPELORE", file,
			"en_US", Utils.asList("&7Type of item"),
			"fr_FR", Utils.asList("&7Type d'item"),
			"it_IT", Utils.asList("&7Tipo di oggetto")
			);

	public static final Text GUI_GENERIC_EDITOR_ITEM_DURABILITYLORE = new Text(
			"GUI_GENERIC_EDITOR_ITEM_DURABILITYLORE", file,
			"en_US", Utils.asList("&7Durability of item"),
			"fr_FR", Utils.asList("&7Durabilité de l'item"),
			"it_IT", Utils.asList("&7Durabilità dell'oggetto")
			);

	public static final Text GUI_GENERIC_EDITOR_ITEM_UNBREAKABLELORE = new Text(
			"GUI_GENERIC_EDITOR_ITEM_UNBREAKABLELORE", file,
			"en_US", Utils.asList("&7Does the item has the unbreakable tag"),
			"fr_FR", Utils.asList("&7Est-ce que l'item a le tag unbreakable"),
			"it_IT", Utils.asList("&7L'oggetto ha il tag unbreakable")
			);

	public static final Text GUI_GENERIC_EDITOR_ITEM_AMOUNTLORE = new Text(
			"GUI_GENERIC_EDITOR_ITEM_AMOUNTLORE", file,
			"en_US", Utils.asList("&7Amount of items"),
			"fr_FR", Utils.asList("&7Nombre d'items"),
			"it_IT", Utils.asList("&7Numero di oggetti")
			);

	public static final Text GUI_GENERIC_EDITOR_ITEM_NAMELORE = new Text(
			"GUI_GENERIC_EDITOR_ITEM_NAMELORE", file,
			"en_US", Utils.asList("&7Display name of item"),
			"fr_FR", Utils.asList("&7Nom d'affichage de l'item"),
			"it_IT", Utils.asList("&7Mostra il nome dell'oggetto")
			);

	public static final Text GUI_GENERIC_EDITOR_ITEM_LORELORE = new Text(
			"GUI_GENERIC_EDITOR_ITEM_LORELORE", file,
			"en_US", Utils.asList("&7Lore of item"),
			"fr_FR", Utils.asList("&7Description de l'item"),
			"it_IT", Utils.asList("&7Descrizione dell'oggetto")
			);

	public static final Text GUI_GENERIC_EDITOR_ITEM_ENCHANTSLORE = new Text(
			"GUI_GENERIC_EDITOR_ITEM_ENCHANTSLORE", file,
			"en_US", Utils.asList("&7List of item enchants", "&7Line format : &6{enchantment},{level}"),
			"fr_FR", Utils.asList("&7Liste des enchantements de l'item", "&7Format de ligne : &6{enchantement},{niveau}"),
			"it_IT", Utils.asList("&7Lista di incantesimi dell'oggetto", "&7Formato linee: &6{enchantement},{niveau}")
			);

	public static final Text GUI_GENERIC_EDITOR_ITEM_NBTLORE = new Text(
			"GUI_GENERIC_EDITOR_ITEM_NBTLORE", file,
			"en_US", Utils.asList("&7NBT tag of item, base64 encoded"),
			"fr_FR", Utils.asList("&7NBT tag de l'item, encodé en base64"),
			"it_IT", Utils.asList("&7NBT tag dell'oggetto, base64 encoded")
			);

	public static final Text GUI_GENERIC_EDITOR_ITEM_MUSTHAVEINHANDLORE = new Text(
			"GUI_GENERIC_EDITOR_ITEM_MUSTHAVEINHANDLORE", file,
			"en_US", Utils.asList("&7Should the item be hold in hand"),
			"fr_FR", Utils.asList("&7Est-ce que l'item doit être tenu en main"),
			"it_IT", Utils.asList("&7Se l'oggetto si tiene in mano")
			);

	public static final Text GUI_GENERIC_EDITOR_ITEM_REMOVEAFTERACTIONLORE = new Text(
			"GUI_GENERIC_EDITOR_ITEM_REMOVEAFTERACTIONLORE", file,
			"en_US", Utils.asList("&7Should the item be removed after action"),
			"fr_FR", Utils.asList("&7Est-ce que l'item doit être supprimé après l'action"),
			"it_IT", Utils.asList("&7Se l'oggetto deve essere cancellato dopo l'azione")
			);

}

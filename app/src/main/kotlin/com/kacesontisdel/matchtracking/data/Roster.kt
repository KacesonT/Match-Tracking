package com.kacesontisdel.matchtracking.data

// id = what we store per game AND the icon filename suffix (chr_<id>.png / stg_<id>.png).
data class Fighter(val id: String, val name: String)

data class Stage(val id: String, val name: String)

val FIGHTERS: List<Fighter> = listOf(
  Fighter("mario", "Mario"),
  Fighter("donkey_kong", "Donkey Kong"),
  Fighter("link", "Link"),
  Fighter("samus", "Samus"),
  Fighter("dark_samus", "Dark Samus"),
  Fighter("yoshi", "Yoshi"),
  Fighter("kirby", "Kirby"),
  Fighter("fox", "Fox"),
  Fighter("pikachu", "Pikachu"),
  Fighter("luigi", "Luigi"),
  Fighter("ness", "Ness"),
  Fighter("captain_falcon", "Captain Falcon"),
  Fighter("jigglypuff", "Jigglypuff"),
  Fighter("peach", "Peach"),
  Fighter("daisy", "Daisy"),
  Fighter("bowser", "Bowser"),
  Fighter("ice_climbers", "Ice Climbers"),
  Fighter("sheik", "Sheik"),
  Fighter("zelda", "Zelda"),
  Fighter("dr_mario", "Dr. Mario"),
  Fighter("pichu", "Pichu"),
  Fighter("falco", "Falco"),
  Fighter("marth", "Marth"),
  Fighter("lucina", "Lucina"),
  Fighter("young_link", "Young Link"),
  Fighter("ganondorf", "Ganondorf"),
  Fighter("mewtwo", "Mewtwo"),
  Fighter("roy", "Roy"),
  Fighter("chrom", "Chrom"),
  Fighter("game_and_watch", "Mr. Game & Watch"),
  Fighter("meta_knight", "Meta Knight"),
  Fighter("pit", "Pit"),
  Fighter("dark_pit", "Dark Pit"),
  Fighter("zero_suit_samus", "Zero Suit Samus"),
  Fighter("wario", "Wario"),
  Fighter("snake", "Snake"),
  Fighter("ike", "Ike"),
  Fighter("pokemon_trainer", "Pokémon Trainer"),
  Fighter("diddy_kong", "Diddy Kong"),
  Fighter("lucas", "Lucas"),
  Fighter("sonic", "Sonic"),
  Fighter("king_dedede", "King Dedede"),
  Fighter("olimar", "Olimar"),
  Fighter("lucario", "Lucario"),
  Fighter("rob", "R.O.B."),
  Fighter("toon_link", "Toon Link"),
  Fighter("wolf", "Wolf"),
  Fighter("villager", "Villager"),
  Fighter("mega_man", "Mega Man"),
  Fighter("wii_fit_trainer", "Wii Fit Trainer"),
  Fighter("rosalina_and_luma", "Rosalina & Luma"),
  Fighter("little_mac", "Little Mac"),
  Fighter("greninja", "Greninja"),
  Fighter("mii_brawler", "Mii Brawler"),
  Fighter("mii_swordfighter", "Mii Swordfighter"),
  Fighter("mii_gunner", "Mii Gunner"),
  Fighter("palutena", "Palutena"),
  Fighter("pac_man", "Pac-Man"),
  Fighter("robin", "Robin"),
  Fighter("shulk", "Shulk"),
  Fighter("bowser_jr", "Bowser Jr."),
  Fighter("duck_hunt", "Duck Hunt"),
  Fighter("ryu", "Ryu"),
  Fighter("ken", "Ken"),
  Fighter("cloud", "Cloud"),
  Fighter("corrin", "Corrin"),
  Fighter("bayonetta", "Bayonetta"),
  Fighter("inkling", "Inkling"),
  Fighter("ridley", "Ridley"),
  Fighter("simon", "Simon"),
  Fighter("richter", "Richter"),
  Fighter("king_k_rool", "King K. Rool"),
  Fighter("isabelle", "Isabelle"),
  Fighter("incineroar", "Incineroar"),
  // DLC
  Fighter("piranha_plant", "Piranha Plant"),
  Fighter("joker", "Joker"),
  Fighter("hero", "Hero"),
  Fighter("banjo_and_kazooie", "Banjo & Kazooie"),
  Fighter("terry", "Terry"),
  Fighter("byleth", "Byleth"),
  Fighter("min_min", "Min Min"),
  Fighter("steve", "Steve"),
  Fighter("sephiroth", "Sephiroth"),
  Fighter("pyra_mythra", "Pyra/Mythra"),
  Fighter("kazuya", "Kazuya"),
  Fighter("sora", "Sora"),
)

val STAGES: List<Stage> = listOf(
  Stage("battlefield", "Battlefield"),
  Stage("small_battlefield", "Small Battlefield"),
  Stage("final_destination", "Final Destination"),
  Stage("pokemon_stadium_2", "Pokémon Stadium 2"),
  Stage("smashville", "Smashville"),
  Stage("town_and_city", "Town and City"),
  Stage("kalos_pokemon_league", "Kalos Pokémon League"),
  Stage("hollow_bastion", "Hollow Bastion"),
  Stage("yoshis_story", "Yoshi's Story"),
)

private val fightersById = FIGHTERS.associateBy { it.id }
private val stagesById = STAGES.associateBy { it.id }

// Falls back to the raw id so an unknown id still shows something.
fun fighterName(id: String): String = fightersById[id]?.name ?: id
fun stageName(id: String): String = stagesById[id]?.name ?: id

// Fails at app start if the roster is ever mistyped (wrong count or a duplicate id).
// Cheap guard; Step 3's unit tests can also cover it.
private val rosterCheck = check(FIGHTERS.size == 86 && FIGHTERS.map { it.id }.toSet().size == 86) {
  "Roster must be 86 unique fighters, got ${FIGHTERS.size}"
}

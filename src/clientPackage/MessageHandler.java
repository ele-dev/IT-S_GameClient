package clientPackage;

/*
 * written by Elias Geiger
 * 
 * This class is intented do message handling and is only used in a static context
 * Every time the listener thread receives a new message the method inside this class 
 * is called to identify the message type and to decide how to react or what to do with
 * the data insided the newly received message.
 * 
 * This class was mainly introduced to outsource the message handling and not to overload other classes
 * since the handleMessage() Method is relatively long and was freqently extended during development
 * 
 */

import java.awt.Color;

import GamePieces.DetonatorPiece;
import GamePieces.EMPPiece;
import GamePieces.FlamethrowerPiece;
import GamePieces.GamePiece;
import GamePieces.GunnerPiece;
import GamePieces.RapidElectroPiece;
import GamePieces.RocketLauncherPiece;
import GamePieces.ShotgunPiece;
import GamePieces.SniperPiece;
import GamePieces.TazerPiece;
import Stage.BoardRectangle;
import Stage.Commons;
import Stage.ProjectFrame;
import Stage.StagePanel;
import menueGui.GameState;

import networking.*;

public class MessageHandler {

	public static void handleMessage(GenericMessage msg)
	{
		// Read message id to get the type of the message
		int id = msg.getMessageID();
		
		switch(id)
		{
			// Message that contains game statistics about the player account
			case GenericMessage.MSG_ACCOUNT_STATS:
			{
				// First parse the message into the right format
				MsgAccountStats accountStats = (MsgAccountStats) msg;
				
				// Now store the received stats from the message
				GameState.playedMatches = accountStats.getPlayedMatches();
				GameState.money = accountStats.getAccountBalance();
				
				// Show stats on the console for debugging
				System.out.println("Received account stats from the server");
				System.out.println("Played Matches: " + GameState.playedMatches);
				System.out.println("Account Balance: " + GameState.money);
				
				break;
			}
			
			// Message that contains global multiplayer information (no ingame info)
			case GenericMessage.MSG_GAME_DATA:
			{
				// Ignore this mesage if the player isnt't logged in yet
				if(!ProjectFrame.conn.isLoggedIn()) {
					System.err.println("Received invalid game data message from the server!");
				}
				
				// Coerce it into the right format
				MsgGameData gameData = (MsgGameData) msg;
				
				// Read and store the containing state variables (ignore values < 0) 
				if(gameData.getOnlinePlayerCount() >= 0) {
					GameState.onlinePlayers = gameData.getOnlinePlayerCount();
				} 
				if(gameData.getRunningMatchCount() >= 0) {
					GameState.globalRunningMatches = gameData.getRunningMatchCount();
				}
				
				// Show received data on the console for debugging
				System.out.println("Received Game Data from the server");
				System.out.println("Current online players: " + GameState.onlinePlayers);
				System.out.println("Currently running matches: " + GameState.globalRunningMatches);
				
				break;
			}
			
			// A Signal Message that says a match was found
			case GenericMessage.MSG_FOUND_MATCH:
			{
				// Ignore this message if the player wasn't searching for a match
				if(!GameState.isSearching) {
					System.err.println("Received invalid match found message from the server!");
					break;
				}
				
				System.out.println("Match was found --> joining match ...");
				
				// First update the players state flags
				GameState.isSearching = false;
				GameState.isIngame = true;
				
				break;
			}
			
			// Message provides player with neccessary data before the match begins
			case GenericMessage.MSG_MATCH_INFO:
			{
				// Ignore this message if the player isn't in game at the moment
				if(!GameState.isIngame) {
					System.err.println("Received invalid match info message from the server!");
					break;
				}
				
				// First parse the message into the right format
				MsgMatchInfo matchInfo = (MsgMatchInfo) msg;
				
				// Store the match data from the message
				GameState.enemyName = matchInfo.getEnemyPlayerName();
				switch(matchInfo.getTeamColor()) 
				{
					case 1:
					{
						GameState.myTeamColor = Commons.cBlue;
						GameState.enemyTeamColor = Commons.cRed;
						GameState.myTeamIsRed = false;
						break;
					}
				
					case 2:
					{
						GameState.myTeamColor = Commons.cRed;
						GameState.enemyTeamColor = Commons.cBlue;
						GameState.myTeamIsRed = true;
						break;
					}
					
					default:
					{
						GameState.myTeamColor = Color.GRAY;
						break;
					}
				}
				
				// If your are team blue then it's your turn at first
				if(!GameState.myTeamIsRed) {
					GameState.myTurn = true;
				} else {
					GameState.myTurn = false;
				}
				
				// Call methods that assign GamePieces and Fortresses to your team or enemy team
				System.out.println("Received Match data --> navigating to stage panel");
				
				// Navigate to the game panel where the actual game happens
				ProjectFrame.homePanel.closePanel();
				StagePanel.resetMatch("LargeMap");
				ProjectFrame.stagePanel.setVisible(true);
				
				break;
			}
			
			// This message notifies the player that the match is over because the enemy has 
			// left the game volunterally
			case GenericMessage.MSG_ENEMY_SURRENDER:
			{
				// Ignore this message if the player isn't ingame at the moment
				if(!GameState.isIngame) {
					System.err.println("Received invalid enemy surrender message from the server!");
					break;
				}
				
				// show popup message informing that the match is over because the enemy left the game
				System.out.println("The enemy surrendered --> leaving match");
				
				// Update the player states
				GameState.enemySurrender = true;
				if(!GameState.myTeamIsRed) {
					StagePanel.redBase.getDamaged(StagePanel.redBase.getHealth(), 0, true);
				} else {
					StagePanel.blueBase.getDamaged(StagePanel.blueBase.getHealth(), 0, true);
				}
				GameState.isIngame = false;
				GameState.isSearching = false;
				
				// Run the winning detection
				StagePanel.checkIfSomeOneWon();
				
				break;
			}
			
			// This message is received from the server as soon as the enemy has finished his turn
			// When this message is received then our own turn begins 
			case GenericMessage.MSG_BEGIN_TURN:
			{
				// Ignore this message if the player isn't ingame at the moment
				if(!GameState.isIngame) {
					System.err.println("Received invalid begin turn message from the server!");
					break;
				}
				
				// Update the global state variable and show info box 
				GameState.myTurn = true;
				
				// Now update the GUI to switch the turn graphically and process all connected events
				StagePanel.updateTurn();
				
				break;
			}
			
			// This message is received when the enemy is acting and has moved one of his Game Pieces
			// to a new position on the game board
			case GenericMessage.MSG_MAKE_MOVE:
			{
				// Ignore message when we aren't ingame or if it's our turn at the moment
				if(!GameState.isIngame || GameState.myTurn) {
					System.err.println("Received invalid make move message from the server!");
					break;
				}
				
				// Coerce the message into the right format
				MsgMakeMove moveMsg = (MsgMakeMove) msg;
				
				// check for all required attributes to be present (not null)
				if(moveMsg.getMovingPlayerPos() == null || moveMsg.getTargetField() == null) {
					System.err.println("Move message is missing required attributes!");
					break;
				}
				
				// Find the moving GamePiece and it's destination Field on the local game map 
				GamePiece movingGP = GamePiece.getGamePieceFromCoords(moveMsg.getMovingPlayerPos());
				BoardRectangle destinationBR = BoardRectangle.getBoardRectFromCoords(moveMsg.getTargetField());
				
				// Execute the move of the enemy game piece 
				if(destinationBR != null && movingGP != null) {
					movingGP.startMove(destinationBR);
					System.out.println("Executed enemy move action");
				} else {
					System.err.println("Could not execute enemy move action!");
				}
				
				// For debugging
				System.out.println("movingGP: row=" + movingGP.getBoardRect().row + "column=" + movingGP.getBoardRect().column);
				System.out.println("destinationBR: row=" + destinationBR.row + "column="+ destinationBR.column);
				
				break;
			}
			
			// This message is received when the enemy is acting and has attacked one of our GamePieces
			// with one of his 
			case GenericMessage.MSG_ATTACK:
			{
				// Ignore message when we aren't ingame or if it's our turn at the moment
				if(!GameState.isIngame || GameState.myTurn) {
					System.err.println("Received invalid make move message from the server!");
					break;
				}
				
				// Coerce the message into the right format
				MsgAttack attackMsg = (MsgAttack) msg;
				
				// check for all required attributes to be present (not null)
				if(attackMsg.getAttackerPiece() == null || attackMsg.getVicitimPos() == null) {
					System.err.println("Attack message is missing required parameters");
					break;
				}
				
				// Find the attacker gmaePiece and the victim game piece on the local game map
				GamePiece attackerGP = GamePiece.getGamePieceFromCoords(attackMsg.getAttackerPiece());
				BoardRectangle victimBR = BoardRectangle.getBoardRectFromCoords(attackMsg.getVicitimPos());
				
				// Check for invalid arguments 
				if(attackerGP == null) {
					System.err.println("Could not execute the enemy attack action!");
					System.err.println("  --> invalid attacker");
					break;
				}
				
				if(victimBR == null) {
					System.err.println("Could not execute the enemy attack action!");
					System.err.println("  --> invalid target field");
					break;
				}
				
				// Execute the enemy's attack
				attackerGP.startAttack(victimBR);
				
				// For debugging
				System.out.println("attackerGP: row=" + attackerGP.getBoardRect().row + " column=" + attackerGP.getBoardRect().column);
				System.out.println("victimGP: row=" + victimBR.row + " column=" + victimBR.column);
				
				// Check if someone has won the game 
				StagePanel.checkIfSomeOneWon();
				
				break;
			}
			
			// This message is received when a new game piece was has spawned on the game map
			case GenericMessage.MSG_SPAWN_GAMEPIECE:
			{
				// Ignore message when we aren't ingame 
				if(!GameState.isIngame) {
					System.err.println("Received invalid spawn gamepiece message from the server!");
					break;
				}
				
				// Coerce the message into the right format
				MsgSpawnGamepiece spawnGPMsg = (MsgSpawnGamepiece) msg;
				
				// Check if the contained game piece is real and valid
				String gpClass = spawnGPMsg.getGamePieceClass();
				Color teamColor = spawnGPMsg.getTeamColor();
				boolean isRed = false;
				if(teamColor.equals(Color.RED)) {
					isRed = true;
				}
				BoardRectangle spawnPoint = BoardRectangle.getBoardRectFromCoords(spawnGPMsg.getFieldCoordinates());
				if(gpClass.equals("undefined") || !(teamColor.equals(Color.BLUE) || teamColor.equals(Color.RED))
						|| spawnPoint == null) {
					System.err.println("Received spawn gamepiece message with invalid gamePiece data!");
					break;
				}
				
				// create and add the gamepiece to the global list
				GamePiece spawnedGP = null;
				switch(gpClass) 
				{
					case "GunnerPiece":
					{
						spawnedGP = new GunnerPiece(isRed, spawnPoint);
						break;
					}
					
					case "SniperPiece":
					{
						spawnedGP = new SniperPiece(isRed, spawnPoint);
						break;
					}
					
					case "FlameThrowerPiece":
					{
						spawnedGP = new FlamethrowerPiece(isRed, spawnPoint);
						break;
					}
					
					case "DetonatorPiece":
					{
						spawnedGP = new DetonatorPiece(isRed, spawnPoint);
						break;
					}
					
					case "TazerPiece":
					{
						spawnedGP = new TazerPiece(isRed, spawnPoint);
						break;
					}
					
					case "RocketLauncherPiece":
					{
						spawnedGP = new RocketLauncherPiece(isRed, spawnPoint);
						break;
					}
					
					case "EMPPiece":
					{
						spawnedGP = new EMPPiece(isRed, spawnPoint);
						break;
					}
					
					case "RapidElectroPiece":
					{
						spawnedGP = new RapidElectroPiece(isRed, spawnPoint);
						break;
					}
					
					case "ShotgunPiece":
					{
						spawnedGP = new ShotgunPiece(isRed, spawnPoint);
						break;
					}
					
					default:
					{
						break;
					}
				}
				
				if(spawnedGP != null) {
					StagePanel.gamePieces.add(spawnedGP);
					System.out.println("Received spawn game piece message. Added Piece to the global list");
				} else {
					System.err.println("Invalid game piece class: " + gpClass);
				}
				
				break;
			}
			
			// If the message type didn't match any of these types
			default:
			{
				System.err.println("Received message of unknown type!");
				break;
			}
		}
	}
}

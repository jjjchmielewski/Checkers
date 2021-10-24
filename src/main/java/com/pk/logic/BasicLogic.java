package com.pk.logic;

import java.util.List;

import javax.management.InvalidAttributeValueException;

import java.util.ArrayList;

import com.pk.logic.exceptions.BadBoardGiven;
import com.pk.logic.exceptions.MandatoryKillMove;
import com.pk.logic.exceptions.MoreThanOneMoveMade;
import com.pk.logic.exceptions.MoveOnAlreadyTakenSpace;
import com.pk.logic.exceptions.VerticalOrHorizontalMove;

public class BasicLogic implements Logic {
  public BasicLogic(List<List<Piece>> board) throws BadBoardGiven {
    // fill the board
    if (board.size() < 2 || board.size() % 2 != 0) {
      throw new BadBoardGiven("This format of board is not supported\n");
    }
    this.board = new ArrayList<>(board.size());
    for (int i = 0; i < board.size(); ++i) {
      this.board.add(new ArrayList<>(board.get(i)));
    }
    // analyze white and black positions for the first time
    this.white = findAllWhite(board);
    this.black = findAllBlack(board);
  }

  public Boolean update(List<List<Piece>> board)
      throws MandatoryKillMove, VerticalOrHorizontalMove, MoreThanOneMoveMade, MoveOnAlreadyTakenSpace {
    List<PiecePosition> white = findAllWhite(board);
    List<PiecePosition> black = findAllBlack(board);
    List<Integer> positions;

    positions = findOneProperMove(board);

    // when there will be sucessful update then push this.board to this.boardOld
    this.boardOld = this.board;
    this.whiteOld = this.white;
    this.blackOld = this.black;

    return false;
  }

  private List<Integer> findOneProperMove(List<List<Piece>> board)
      throws MoreThanOneMoveMade, MoveOnAlreadyTakenSpace, VerticalOrHorizontalMove, MandatoryKillMove {
    List<PiecePosition> white = findAllWhite(board);
    List<PiecePosition> black = findAllBlack(board);
    Boolean wasKillMove;
    // first check for rules violation then we can return proper move
    isNonDiagonalMove(white, black);
    wasKillMove = wasKillMove(board);
    // if it wasn't kill move there is need to check MandatoryKillMove as this
    // condition haven't been accomplished
    if (Boolean.FALSE.equals(wasKillMove)) {
      if (Boolean.TRUE.equals(isKillMoveAvaliable(white))) {
        throw new MandatoryKillMove();
      } else {

      }
    }
    return new ArrayList<>();
  }

  private Boolean isKillMove(List<List<Piece>> board) {

    return false;
  }

  /*
   * Changes board to contain KILLED_<COLOR> in the board. If kill move was
   * avaliable and not taken throws MandatoryKillMove. Checks whether kill move
   * was possible or not.
   *
   * @throws MandatoryKillMove
   */
  private Boolean wasKillMove(List<List<Piece>> board) {
    for (int i = 0; i < this.board.size(); ++i) {
      for (int j = 0; j < this.board.get(i).size(); ++j) {
        if (boardOld.get(i).get(j) == Piece.KILLED_BLACK || boardOld.get(i).get(j) == Piece.KILLED_WHITE) {
          return true;
        }
      }
    }
    return false;
  }

  private Boolean isKillMoveAvaliable(List<PiecePosition> white) {
    Integer whiteMove = -1;
    // We nned to have bordering pawns and with kings to check whole diagonals
    // Check for Kings
    whiteMove = isWhiteMove(white);
    if (whiteMove == 1) {
      if (Boolean.FALSE.equals(isKillMoveAvaliableWhiteKings())
          && Boolean.FALSE.equals(isKillMoveAvaliableWhitePawns())) {
        return false;
      }
    } else {
      if (Boolean.FALSE.equals(isKillMoveAvaliableBlackKings())
          && Boolean.FALSE.equals(isKillMoveAvaliableBlackPawns())) {
        return false;
      }
    }
    return true;
  }

  private Boolean isKillMoveAvaliableWhiteKings() {
    for (int i = 0; i < this.white.size(); ++i) {
      if (this.white.get(i).getAffiliation() == Piece.WHITE_KING) {
        for (int offset = 0; offset < this.board.size(); ++offset) {
          if (Boolean.TRUE.equals(isKillMoveAvaliableLeftUpWhite(i, offset))
              || Boolean.TRUE.equals(isKillMoveAvaliableRightUpWhite(i, offset))
              || Boolean.TRUE.equals(isKillMoveAvaliableRightDownWhite(i, offset))
              || Boolean.TRUE.equals(isKillMoveAvaliableLeftDownWhite(i, offset))) {
            return true;
          }
        }
      }
    }
    return false;
  }

  private Boolean isKillMoveAvaliableWhitePawns() {
    int offset = 1;
    for (int i = 0; i < this.white.size(); ++i) {
      if (this.white.get(i).getAffiliation() == Piece.WHITE_PAWN
          && (Boolean.TRUE.equals(isKillMoveAvaliableLeftUpWhite(i, offset))
              || Boolean.TRUE.equals(isKillMoveAvaliableRightUpWhite(i, offset)))) {
        return true;
      }
    }
    return false;
  }

  private Boolean isKillMoveAvaliableBlackKings() {
    for (int i = 0; i < this.black.size(); ++i) {
      if (this.black.get(i).getAffiliation() == Piece.BLACK_KING) {
        for (int offset = 0; offset < this.board.size(); ++offset) {
          if (Boolean.TRUE.equals(isKillMoveAvaliableLeftUpBlack(i, offset))
              || Boolean.TRUE.equals(isKillMoveAvaliableRightUpBlack(i, offset))
              || Boolean.TRUE.equals(isKillMoveAvaliableRightDownBlack(i, offset))
              || Boolean.TRUE.equals(isKillMoveAvaliableLeftDownBlack(i, offset))) {
            return true;
          }
        }
      }
    }
    return false;
  }

  private Boolean isKillMoveAvaliableBlackPawns() {
    int offset = 1;
    for (int i = 0; i < this.black.size(); ++i) {
      if (this.black.get(i).getAffiliation() == Piece.BLACK_KING
          && (Boolean.TRUE.equals(isKillMoveAvaliableRightDownBlack(i, offset))
              || Boolean.TRUE.equals(isKillMoveAvaliableLeftDownBlack(i, offset)))) {
        return true;
      }
    }
    return false;
  }

  private Boolean isKillMoveAvaliableLeftUpWhite(Integer index, Integer offset) {
    try {
      Piece temp = getPieceLeftUpOffset(this.board, this.white.get(index).getX(), this.white.get(index).getY(), offset);
      if (temp.equals(Piece.BLACK_KING) || temp.equals(Piece.BLACK_PAWN)) {
        temp = getPieceLeftUpOffset(this.board, this.white.get(index).getX(), this.white.get(index).getY(), offset + 1);
        if (temp.equals(Piece.EMPTY)) {
          return true;
        }
      } else if (temp.equals(Piece.WHITE_KING) || temp.equals(Piece.WHITE_PAWN)) {
        return false;
      }
    } catch (InvalidAttributeValueException e) {
      return false;
    }
    return false;
  }

  private Boolean isKillMoveAvaliableRightUpWhite(Integer index, Integer offset) {
    try {
      Piece temp = getPieceRightUpOffset(this.board, this.white.get(index).getX(), this.white.get(index).getY(),
          offset);
      if (temp.equals(Piece.BLACK_KING) || temp.equals(Piece.BLACK_PAWN)) {
        temp = getPieceRightUpOffset(this.board, this.white.get(index).getX(), this.white.get(index).getY(),
            offset + 1);
        if (temp.equals(Piece.EMPTY)) {
          return true;
        }
      } else if (temp.equals(Piece.WHITE_KING) || temp.equals(Piece.WHITE_PAWN)) {
        return false;
      }
    } catch (InvalidAttributeValueException e) {
      return false;
    }
    return false;
  }

  private Boolean isKillMoveAvaliableRightDownWhite(Integer index, Integer offset) {
    try {
      Piece temp = getPieceRightDownOffset(this.board, this.white.get(index).getX(), this.white.get(index).getY(),
          offset);
      if (temp.equals(Piece.BLACK_KING) || temp.equals(Piece.BLACK_PAWN)) {
        temp = getPieceRightDownOffset(this.board, this.white.get(index).getX(), this.white.get(index).getY(),
            offset + 1);
        if (temp.equals(Piece.EMPTY)) {
          return true;
        }
      } else if (temp.equals(Piece.WHITE_KING) || temp.equals(Piece.WHITE_PAWN)) {
        return false;
      }
    } catch (InvalidAttributeValueException e) {
      return false;
    }
    return false;
  }

  private Boolean isKillMoveAvaliableLeftDownWhite(Integer index, Integer offset) {
    try {
      Piece temp = getPieceLeftDownOffset(this.board, this.white.get(index).getX(), this.white.get(index).getY(),
          offset);
      if (temp.equals(Piece.BLACK_KING) || temp.equals(Piece.BLACK_PAWN)) {
        temp = getPieceLeftDownOffset(this.board, this.white.get(index).getX(), this.white.get(index).getY(),
            offset + 1);
        if (temp.equals(Piece.EMPTY)) {
          return true;
        }
      } else if (temp.equals(Piece.WHITE_KING) || temp.equals(Piece.WHITE_PAWN)) {
        return false;
      }
    } catch (InvalidAttributeValueException e) {
      return false;
    }
    return false;
  }

  private Boolean isKillMoveAvaliableLeftUpBlack(Integer index, Integer offset) {
    try {
      Piece temp = getPieceLeftUpOffset(this.board, this.black.get(index).getX(), this.black.get(index).getY(), offset);
      if (temp.equals(Piece.WHITE_KING) || temp.equals(Piece.WHITE_PAWN)) {
        temp = getPieceLeftUpOffset(this.board, this.black.get(index).getX(), this.black.get(index).getY(), offset + 1);
        if (temp.equals(Piece.EMPTY)) {
          return true;
        }
      } else if (temp.equals(Piece.BLACK_KING) || temp.equals(Piece.BLACK_PAWN)) {
        return false;
      }
    } catch (InvalidAttributeValueException e) {
      return false;
    }
    return false;
  }

  private Boolean isKillMoveAvaliableRightUpBlack(Integer index, Integer offset) {
    try {
      Piece temp = getPieceRightUpOffset(this.board, this.black.get(index).getX(), this.black.get(index).getY(),
          offset);
      if (temp.equals(Piece.WHITE_KING) || temp.equals(Piece.WHITE_PAWN)) {
        temp = getPieceRightUpOffset(this.board, this.black.get(index).getX(), this.black.get(index).getY(),
            offset + 1);
        if (temp.equals(Piece.EMPTY)) {
          return true;
        }
      } else if (temp.equals(Piece.BLACK_KING) || temp.equals(Piece.BLACK_PAWN)) {
        return false;
      }
    } catch (InvalidAttributeValueException e) {
      return false;
    }
    return false;
  }

  private Boolean isKillMoveAvaliableRightDownBlack(Integer index, Integer offset) {
    try {
      Piece temp = getPieceRightDownOffset(this.board, this.black.get(index).getX(), this.black.get(index).getY(),
          offset);
      if (temp.equals(Piece.WHITE_KING) || temp.equals(Piece.WHITE_PAWN)) {
        temp = getPieceRightDownOffset(this.board, this.black.get(index).getX(), this.black.get(index).getY(),
            offset + 1);
        if (temp.equals(Piece.EMPTY)) {
          return true;
        }
      } else if (temp.equals(Piece.BLACK_KING) || temp.equals(Piece.BLACK_PAWN)) {
        return false;
      }
    } catch (InvalidAttributeValueException e) {
      return false;
    }
    return false;
  }

  private Boolean isKillMoveAvaliableLeftDownBlack(Integer index, Integer offset) {
    try {
      Piece temp = getPieceLeftDownOffset(this.board, this.black.get(index).getX(), this.black.get(index).getY(),
          offset);
      if (temp.equals(Piece.WHITE_KING) || temp.equals(Piece.WHITE_PAWN)) {
        temp = getPieceLeftDownOffset(this.board, this.black.get(index).getX(), this.black.get(index).getY(),
            offset + 1);
        if (temp.equals(Piece.EMPTY)) {
          return true;
        }
      } else if (temp.equals(Piece.BLACK_KING) || temp.equals(Piece.BLACK_PAWN)) {
        return false;
      }
    } catch (InvalidAttributeValueException e) {
      return false;
    }
    return false;
  }

  private Piece getPieceLeftUpOffset(List<List<Piece>> board, Integer x, Integer y, Integer offset)
      throws InvalidAttributeValueException {
    if (x - offset >= 0 && y - offset >= 0) {
      return board.get(x).get(y);
    } else {
      throw new InvalidAttributeValueException();
    }
  }

  private Piece getPieceRightUpOffset(List<List<Piece>> board, Integer x, Integer y, Integer offset)
      throws InvalidAttributeValueException {
    if (x + offset < board.size() && y - offset >= 0) {
      return board.get(x).get(y);
    } else {
      throw new InvalidAttributeValueException();
    }
  }

  private Piece getPieceRightDownOffset(List<List<Piece>> board, Integer x, Integer y, Integer offset)
      throws InvalidAttributeValueException {
    if (x + offset < board.size() && y + offset < board.size()) {
      return board.get(x).get(y);
    } else {
      throw new InvalidAttributeValueException();
    }
  }

  private Piece getPieceLeftDownOffset(List<List<Piece>> board, Integer x, Integer y, Integer offset)
      throws InvalidAttributeValueException {
    if (x + offset >= 0 && y + offset < board.size()) {
      return board.get(x).get(y);
    } else {
      throw new InvalidAttributeValueException();
    }
  }

  private Integer isWhiteMove(List<PiecePosition> white) {
    Boolean found = false;
    for (int i = 0; i < this.white.size(); ++i) {
      for (int j = 0; j < white.size(); ++j) {
        if (this.white.get(i).getX() == white.get(j).getX() && this.white.get(i).getY() == white.get(j).getX()) {
          found = true;
          break;
        }
      }
      if (Boolean.TRUE.equals(found)) {
        found = false;
      } else {
        return 1;
      }
    }
    return 0;
  }

  public String toString() {
    StringBuilder result = new StringBuilder();
    for (int i = 0; i < this.board.size(); ++i) {
      for (int j = 0; j < this.board.get(i).size(); ++j) {
        switch (this.board.get(i).get(j)) {
        case EMPTY:
          result.append("0");
          break;
        case WHITE_KING:
          result.append("I");
          break;
        case WHITE_PAWN:
          result.append("W");
          break;
        case BLACK_PAWN:
          result.append("B");
          break;
        case BLACK_KING:
          result.append("X");
          break;
        case KILLED_BLACK:
          result.append("D");
          break;
        case KILLED_WHITE:
          result.append("S");
          break;
        }
      }
      result.append("\n");
    }
    return result.toString();
  }

  private List<PiecePosition> findAllWhite(List<List<Piece>> board) {
    List<PiecePosition> whiteLocal = new ArrayList<>();
    for (int i = 0; i < board.size(); ++i) {
      for (int j = 0; j < board.get(i).size(); ++j) {
        if (board.get(i).get(j).equals(Piece.WHITE_KING) || board.get(i).get(j).equals(Piece.WHITE_PAWN)) {
          whiteLocal.add(new PiecePosition(Integer.valueOf(i), Integer.valueOf(j), board.get(i).get(j)));
        }
      }
    }
    return whiteLocal;
  }

  private List<PiecePosition> findAllBlack(List<List<Piece>> board) {
    ArrayList<PiecePosition> blackLocal = new ArrayList<>();
    for (int i = 0; i < board.size(); ++i) {
      for (int j = 0; j < board.get(i).size(); ++j) {
        if (board.get(i).get(j).equals(Piece.BLACK_KING) || board.get(i).get(j).equals(Piece.BLACK_PAWN)) {
          blackLocal.add(new PiecePosition(Integer.valueOf(i), Integer.valueOf(j), board.get(i).get(j)));
        }
      }
    }
    return blackLocal;
  }

  private void isNonDiagonalMove(List<PiecePosition> white, List<PiecePosition> black) throws VerticalOrHorizontalMove {

    for (int i = 0; i < white.size(); ++i) {
      if (white.get(i).getX() % 2 == 1 && white.get(i).getY() % 2 == 0
          || white.get(i).getX() % 2 == 0 && white.get(i).getY() % 2 == 1) {
        throw new VerticalOrHorizontalMove("White made illegal move\n");
      }
    }

    for (int i = 0; i < black.size(); ++i) {
      if (black.get(i).getX() % 2 == 1 && black.get(i).getY() % 2 == 0
          || black.get(i).getX() % 2 == 0 && black.get(i).getY() % 2 == 1) {
        throw new VerticalOrHorizontalMove("Black made illegal move\n");
      }
    }
  }

  private class PiecePosition {
    public PiecePosition(Integer x, Integer y, Piece affiliation) {
      this.x = x;
      this.y = y;
      this.affiliation = affiliation;
    }

    public Integer getX() {
      return x;
    }

    public Integer getY() {
      return y;
    }

    public Piece getAffiliation() {
      return affiliation;
    }

    public void setX(Integer x) {
      this.x = x;
    }

    public void setY(Integer y) {
      this.y = y;
    }

    public void setAffiliation(Piece affiliation) {
      this.affiliation = affiliation;
    }

    private Integer x;
    private Integer y;
    private Piece affiliation;
  }

  private List<PiecePosition> white;
  private List<PiecePosition> black;
  private List<PiecePosition> whiteOld;
  private List<PiecePosition> blackOld;
  private List<List<Piece>> board;
  private List<List<Piece>> boardOld;
}
import Interfaces.ICard;

public class Card implements ICard {

    //#region Attributes

    private final int denomination;

    //#endregion

    //#region Methods

    public Card(int denomination) {
        this.denomination = denomination;
    }

    @Override
    public int getDenomination() {
        // Return the denomination (face value) of the card
        return denomination;
    }

    //#endregion
}
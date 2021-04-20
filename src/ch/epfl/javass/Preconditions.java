package ch.epfl.javass;

/**
 * Vérifie des conditions pré-établies.
 * 
 * @author Mohamed Saad Eddine El Moutaouakil (284843)
 * @author Taha Zakariya (288526)
 *
 */
public final class Preconditions {

    private Preconditions() {
    }

    /**
     * Lève l'exception IllegalArgumentException si son argument est faux, et ne
     * fait rien sinon
     * 
     * @param b
     *            la condition à vérifier
     * @throws IllegalArgumentException
     *             si la condition est fausse.
     */
    public static void checkArgument(boolean b) {
        if (!b)
            throw new IllegalArgumentException();
    }

    /**
     * Lève l'exception IndexOutOfBoundsException si l'index donné est négatif
     * ou supérieur ou égal à size, et le retourne tel quel sinon
     * 
     * @param index
     *            l'indice
     * @param size
     *            la taille
     * @throws IndexOutOfBoundsException
     *             si l'index donné est négatif ou supérieur ou égal à size
     * @return l'indice tel qu'il est
     */
    public static int checkIndex(int index, int size) {
        if (index < 0 || index >= size)
            throw new IndexOutOfBoundsException();
        else
            return index;
    }

}

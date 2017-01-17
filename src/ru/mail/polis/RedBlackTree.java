import java.util.*;

//TODO: write code here
public class RedBlackTree<E extends Comparable<E>> implements ISortedSet<E> {

    // Добавляем логическую переменную экземпляров,
    //  правда, если вершина красная,
    //  ложь, если вершина черная.
    private static final boolean RED   = true;
    private static final boolean BLACK = false;

    private Node root;

    private class Node {
        private E key; //  Ключ
        private Node left, right; //  Поддеревья, левое и правое соответственно
        private boolean color; //  Цвет ссылки на данный узел

        public Node(E key, boolean color, int size) {
            this.key = key;
            this.color = color;
        }
    }

    //  Метод, проверяющий цвет ссылки на узел из его родителя
    //  Цвет узла - это цвет указывающей на него ссылки.
    private boolean isRed(Node x) {
        if (x == null) return false;
        return x.color == RED;
    }

    private int size;
    private final Comparator<E> comparator;

    public RedBlackTree() {
        this.comparator = null;
    }

    public RedBlackTree(Comparator<E> comparator) {
        this.comparator = comparator;
    }


    @Override
    public E first() {
        if (isEmpty()) {
            throw new NoSuchElementException("NULL");
        }
        Node tmp = root;
        while (tmp.left != null) {
            tmp = tmp.left;
        }
        return tmp.key;
    }

    @Override
    public E last() {
        if (isEmpty()) {
            throw new NoSuchElementException("NULL");
        }
        Node tmp = root;
        while (tmp.right != null) {
            tmp = tmp.right;
        }
        return tmp.key;
    }

    @Override
    public List<E> inorderTraverse() {
        List<E> list = new ArrayList<E>(size);
        inorderTraverse(root, list);
        return list;
    }
    private void inorderTraverse(Node curr, List<E> list) {
        if (curr == null) {
            return;
        }
        inorderTraverse(curr.left, list);
        list.add(curr.key);
        inorderTraverse(curr.right, list);
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return root==null;
    }

    // Проверка вхождения элемента во множество
    @Override
    public boolean contains(E value) {
        if (value == null) {
            throw new NullPointerException("NULL");
        }
        if (root != null) {
            Node tmp = root;
            while (tmp != null) {
                int cmp = compare(tmp.key, value);
                if (cmp == 0) {
                    return true;
                } else if (cmp < 0) {
                    tmp = tmp.right;
                } else {
                    tmp = tmp.left;
                }
            }
        }
        return false;
    }

    //  Разворот вправо (левая ссылка узла)
    private Node rotateRight(Node h) {
        Node x = h.left;
        h.left = x.right;
        x.right = h;
        x.color = x.right.color;
        x.right.color = RED;
        return x;
    }

    //  Разворот влево (правая ссылка узла)
    private Node rotateLeft(Node h) {
        Node x = h.right;
        h.right = x.left;
        x.left = h;
        x.color = x.left.color;
        x.left.color = RED;
        return x;
    }

    //  Изменение цвета и дочерних, и корневого узла.
    private void flipColors(Node h) {
        h.color = !h.color;
        h.left.color = !h.left.color;
        h.right.color = !h.right.color;
    }

    private boolean contAdd;

    @Override
    public boolean add(E value) {
        if (value == null) throw new IllegalArgumentException("NULL");
        //  Поиск ключа: если найден - изменяется значение,
        //  если нет - увеличивается размер массива
        contAdd=true;
        root = add(root, value);
        root.color = BLACK;
        if(contAdd==true) size++;
        return contAdd;
    }

    //  Метод вставки в красно-черном дереве похож на метод вставки
    //  в обычном БДП, но имеются 3 условия, которые поддерживают
    //  почти идеальный баланс .
    private Node add(Node h, E key) {
        //  Выполняется обычная вставка с красной связью с родителем
        if (h == null){
            return new Node(key, RED, 1);
        }

        int cmp = key.compareTo(h.key);
        if      (cmp < 0) {
            h.left  = add(h.left,  key);
        }
        else if (cmp > 0) {
            h.right = add(h.right, key);
        }
        else {
            contAdd=false;
        }

        if (isRed(h.right) && !isRed(h.left)) {
            h = rotateLeft(h);
        }
        if (isRed(h.left)  &&  isRed(h.left.left)) {
            h = rotateRight(h);
        }
        if (isRed(h.left)  &&  isRed(h.right)) {
            flipColors(h);
        }

        return h;
    }

    private Node moveRedLeft(Node h) {
        // Если узел красный, а ссылки h.left h.left.left черные,
        // то делаем h.left красным, или его потомков
        flipColors(h);
        if (isRed(h.right.left)) {
            h.right = rotateRight(h.right);
            h = rotateLeft(h);
            flipColors(h);
        }
        return h;
    }

    private Node moveRedRight(Node h) {
        // Если узел красный, а ссылки h.right   h.right.left черные,
        // то закрашиваем h.right красным или один из его потомков
        flipColors(h);
        if (isRed(h.left.left)) {
            h = rotateRight(h);
            flipColors(h);
        }
        return h;
    }

   // private boolean contRemove;

    @Override
    public boolean remove(E value) {
        if (value == null) throw new IllegalArgumentException("NULL");
        if (!contains(value)) return false;

        if (!isRed(root.left) && !isRed(root.right))
            root.color = RED;

        root = delete(root, value);
        if (!isEmpty()){
            root.color = BLACK;
        }
        size--;
        return true;
    }

    private Node delete(Node h, E value) {
        if (h==null) return null;
        int cmp = value.compareTo(h.key);

        if (value.compareTo(h.key) < 0)  {
            if (!isRed(h.left) && !isRed(h.left.left))
                h = moveRedLeft(h);
            h.left = delete(h.left, value);
        }
        else {
            if (isRed(h.left))
                h = rotateRight(h);
            if (value.compareTo(h.key) == 0 && (h.right == null)) {
                return null;
            }
            if (!isRed(h.right) && !isRed(h.right.left))
                h = moveRedRight(h);
            if (value.compareTo(h.key) == 0) {
                Node x = min(h.right);
                h.key = x.key;
                h.right = deleteMin(h.right);
            }
            else h.right = delete(h.right, value);
        }
        return balance(h);
    }

    public void deleteMin() {
        if (isEmpty()) throw new NoSuchElementException("NULL");

        if (!isRed(root.left) && !isRed(root.right)){
            root.color = RED;
        }

        root = deleteMin(root);
        if (!isEmpty()) {
            root.color = BLACK;
        }
    }

    private Node deleteMin(Node h) {
        if (h.left == null){
            return null;
        }

        if (!isRed(h.left) && !isRed(h.left.left)){
            h = moveRedLeft(h);
        }

        h.left = deleteMin(h.left);
        return balance(h);
    }

    //  Проверка дерева на сбалансированность,
    // приведение к сбалансированному виду
    private Node balance(Node h) {
        if (isRed(h.right)) {
            h = rotateLeft(h);
        }
        if (isRed(h.left) && isRed(h.left.left)) {
            h = rotateRight(h);
        }
        if (isRed(h.left) && isRed(h.right)) {
            flipColors(h);
        }

        return h;
    }

    public E min() {
        if (isEmpty()) throw new NoSuchElementException("NULL");
        return min(root).key;
    }

    // Нахождения минимального элемента
    private Node min(Node x) {
        if (x.left == null) {
            return x;
        }
        else {
            return min(x.left);
        }
    }

    private int compare(E v1, E v2) {
        return comparator == null ? v1.compareTo(v2) : comparator.compare(v1, v2);
    }

    public static void main(String[] args) {
       /* RedBlackTree<Integer> tree = new RedBlackTree<>();
        tree.add(10);
        tree.add(5);
        tree.add(15);
        System.out.println(tree.inorderTraverse());
        System.out.println(tree.size);
        System.out.println(tree);
        tree.remove(10);
        tree.remove(15);
        System.out.println(tree.size);
        System.out.println(tree);
        tree.remove(5);
        System.out.println(tree.size);
        System.out.println(tree);
        tree.add(15);
        System.out.println(tree.size);
        System.out.println(tree);

        System.out.println("------------");
        Random rnd = new Random();
        tree = new RedBlackTree<>();
        for (int i = 0; i < 15; i++) {
            tree.add(rnd.nextInt(50));
        }
        System.out.println(tree.inorderTraverse());
        tree = new RedBlackTree<>((v1, v2) -> {
            // Even first
            final int c = Integer.compare(v1 % 2, v2 % 2);
            return c != 0 ? c : Integer.compare(v1, v2);
        });
        for (int i = 0; i < 15; i++) {
            tree.add(rnd.nextInt(50));
        }
        System.out.println(tree.inorderTraverse());*/

    }
}


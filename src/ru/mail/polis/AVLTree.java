/**
 * Created by user on 16.01.2017.
 */

import java.util.*;

//TODO: write code here
public class AVLTree<E extends Comparable<E>> implements ISortedSet<E> {

    private Node root;

    private class Node {
        private final E key;      // Ключ
        private int height;      // высота поддерева
        private Node left;       // левое поддерево
        private Node right;      // правое поддерево

        public Node(E key) {
            this.key = key;
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("N{");
            sb.append("d=").append(key);
            if (left != null) {
                sb.append(", l=").append(left);
            }
            if (right != null) {
                sb.append(", r=").append(right);
            }
            sb.append('}');
            return sb.toString();
        }
    }

    private int size;
    private final Comparator<E> comparator;

    public AVLTree() {
        root = null;
        this.comparator = null;
    }

    public AVLTree(Comparator<E> comparator) {
        this.comparator = comparator;
    }

    @Override
    public E first() {
        if (isEmpty()) {
            throw new NoSuchElementException("NULL");
        }
        Node curr = root;
        while (curr.left != null) {
            curr = curr.left;
        }
        return curr.key;
    }

    @Override
    public E last() {
        if (isEmpty()) {
            throw new NoSuchElementException("NULL");
        }
        Node curr = root;
        while (curr.right != null) {
            curr = curr.right;
        }
        return curr.key;
    }

    @Override
    public List<E> inorderTraverse() {
        List<E> list = new ArrayList<E>(size);
        inorderTraverse(root, list);
        return list;
    }

    // Переопределим метод, возвращающий отсортированное множество
    private void inorderTraverse(Node node, List<E> list) {
        if (node == null) {
            return;
        }
        inorderTraverse(node.left, list);
        list.add(node.key);
        inorderTraverse(node.right, list);
    }

    // Метод, возвращающий размер множества
    @Override
    public int size() {
        return size;
    }

    // Метод, проверяющий множество на пустоту
    @Override
    public boolean isEmpty() {
        return root == null;
    }

    // Проверка вхождения элемента во множество
    @Override
    public boolean contains(E key) {
        if (key == null) {
            throw new NullPointerException("NULL");
        }
        if (root != null) {
            Node curr = root;
            while (curr != null) {
                int cmp = compare(curr.key, key);
                if (cmp == 0) {
                    return true;
                } else if (cmp < 0) {
                    curr = curr.right;
                } else {
                    curr = curr.left;
                }
            }
        }
        return false;
    }

    // Возвращает высоту дерева
    private int height(Node curr) {
        if (curr == null) return -1;
        return curr.height;
    }

    // Фактор, по которому вычисляется необходимость в балансировке:
    // разность левого и правого поддерева не должна быть >
    int balanceFactor(Node curr)    {
        return height(curr.right)-height(curr.left);
    }

    // Заменяем высоту ноды на бОльшую
    void fixHeight(Node curr)
    {
        int hl = height(curr.left);
        int hr = height(curr.right);
        curr.height= (hl>hr?hl:hr)+1;
    }

    // Правый повотор поддерева, выполняется вокруг curr
    private Node rotateRight(Node curr) {
        Node q = curr.left;
        curr.left = q.right;
        q.right = curr;
        fixHeight(curr);
        fixHeight(q);
        return q;
    }

    // Левый поворот поддерева, выполняется вокруг curr
    private Node rotateLeft(Node curr)    {
        Node p = curr.right;
        curr.right = p.left;
        p.left = curr;
        fixHeight(curr);
        fixHeight(p);
        return p;
    }

    // Выполняем балансировку поддерева от узла curr
    private Node balance(Node curr)    {
        fixHeight(curr);
        if( balanceFactor(curr)==2 ) {
            if( balanceFactor(curr.right) < 0 ){
                curr.right = rotateRight(curr.right);
            }
            return rotateLeft(curr);
        }
        if( balanceFactor(curr)==-2 ) {
            if( balanceFactor(curr.left) > 0){
                curr.left = rotateLeft(curr.left);
            }
            return rotateRight(curr);
        }
        return curr;
    }


    @Override
    public boolean add(E key) {
        if (key == null) {
            throw new NullPointerException("NULL");
        }
        if (root == null) {
            root = new Node(key);
        } else {
            Node curr = root;
            while (true) {
                int cmp = compare(curr.key, key);
                if (cmp == 0) {
                    return false;
                } else if (cmp < 0) {
                    if (curr.right != null) {
                        curr = curr.right;
                        balanceFactor(curr);
                    } else {
                        curr.right = new Node(key);
                        break;
                    }
                } else if (cmp > 0) {
                    if (curr.left != null) {
                        curr = curr.left;
                        balanceFactor(curr);
                    } else {
                        curr.left = new Node(key);
                        break;
                    }
                }
            }
        }
        size++;
        return true;
    }

    private boolean contRemove;

    @Override
    public boolean remove(E value) {
        if (value == null) throw new NullPointerException("NULL");

        contRemove=false;
        root = delete(root, value);
        if(contRemove!=false)  {
            size--;
        }
        return contRemove;
    }

    // Удаление элемента, дерево остается сбалансированным
    private Node delete(Node x, E key) {
        if (x == null) {
            return null;
        }
        int cmp = key.compareTo(x.key);
        if (cmp < 0) {
            x.left = delete(x.left, key);
        }
        else if (cmp > 0) {
            x.right = delete(x.right, key);
        }
        else {
            contRemove=true;
            if (x.left == null) {
                return x.right;
            }
            else if (x.right == null) {
                return x.left;
            }
            else {
                Node y = x;
                x = min(y.right);
                x.right = deleteMin(y.right);
                x.left = y.left;
            }
        }
        fixHeight(x);
        return balance(x);
    }

    // Находим минимум
    private Node min(Node x) {
        if (x.left == null) return x;
        return min(x.left);
    }

    // Удаляем минимум, при этом дерево станет сбалансированным
    public void deleteMin() {
        if (isEmpty()) throw new NoSuchElementException("NULL");
        root = deleteMin(root);
    }

    // Удаляем минимум и возвращаем сбалансированное поддерево
    private Node deleteMin(Node x) {
        if (x.left == null) return x.right;
        x.left = deleteMin(x.left);
        fixHeight(x);
        return balance(x);
    }

    private int compare(E v1, E v2) {
        return comparator == null ? v1.compareTo(v2) : comparator.compare(v1, v2);
    }

    public static void main(String[] args) {
       /* AVLTree<Integer> tree = new AVLTree<>();
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
        tree = new AVLTree<>();
        for (int i = 0; i < 15; i++) {
            tree.add(rnd.nextInt(50));
        }
        System.out.println(tree.inorderTraverse());
        tree = new AVLTree<>((v1, v2) -> {
            // Even first
            final int c = Integer.compare(v1 % 2, v2 % 2);
            return c != 0 ? c : Integer.compare(v1, v2);
        });
        for (int i = 0; i < 15; i++) {
            tree.add(rnd.nextInt(50));
        }
        System.out.println(tree.inorderTraverse());*/

       /*ISortedSet<Integer> set = new AVLTree<>();
        set.add(10);
        set.add(1);
        set.add(20);
        System.out.println(set.first());
        System.out.println(set.last());
        System.out.println(set.contains(11));
        System.out.println(set.contains(10));
        System.out.println(set.inorderTraverse());*/
    }
}

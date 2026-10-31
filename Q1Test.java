import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

public class Q1Test {
     public static void main(String[] args) {

          String result, traversal1, traversal2, traversal3;

          traversal1 = "1-2";
          traversal2 = "2-1-3";
          traversal3 = "1-3-2";
          result = verify(traversal1, traversal2, traversal3);
          System.out.println("Traversal 1 : " + traversal1);
          System.out.println("Traversal 2 : " + traversal2);
          System.out.println("Traversal 3 : " + traversal3);
          System.out.println("Expected Result : Invalid traversals");
          System.out.println("  Actual Result : " + result);
          System.out.println();

          traversal1 = "1-2-3";
          traversal2 = "2-1-3";
          traversal3 = "1-3-2";
          result = verify(traversal1, traversal2, traversal3);
          System.out.println("Traversal 1 : " + traversal1);
          System.out.println("Traversal 2 : " + traversal2);
          System.out.println("Traversal 3 : " + traversal3);
          System.out.println(
                    "Expected Result : Traversal 1 - Inorder, Traversal 2 - Preorder, Traversal 3 - Postorder");
          System.out.println("  Actual Result : " + result);
          System.out.println();

          traversal1 = "1-2-3";
          traversal2 = "3-2-1";
          traversal3 = "2-3-1";
          result = verify(traversal1, traversal2, traversal3);
          System.out.println("Traversal 1 : " + traversal1);
          System.out.println("Traversal 2 : " + traversal2);
          System.out.println("Traversal 3 : " + traversal3);
          System.out.println("Expected Result : Invalid traversals");
          System.out.println("  Actual Result : " + result);
          System.out.println();

          traversal1 = "3-1-2-5-4";
          traversal2 = "2-1-4-5-3";
          traversal3 = "1-2-3-4-5";
          result = verify(traversal1, traversal2, traversal3);
          System.out.println("Traversal 1 : " + traversal1);
          System.out.println("Traversal 2 : " + traversal2);
          System.out.println("Traversal 3 : " + traversal3);
          System.out.println(
                    "Expected Result : Traversal 1 - Preorder, Traversal 2 - Postorder, Traversal 3 - Inorder");
          System.out.println("  Actual Result : " + result);
          System.out.println();

          traversal1 = "10-20-30-40-50";
          traversal2 = "20-10-30-40-50";
          traversal3 = "10-50-40-30-20";
          result = verify(traversal1, traversal2, traversal3);
          System.out.println("Traversal 1 : " + traversal1);
          System.out.println("Traversal 2 : " + traversal2);
          System.out.println("Traversal 3 : " + traversal3);
          System.out.println(
                    "Expected Result : Traversal 1 - Inorder, Traversal 2 - Preorder, Traversal 3 - Postorder");
          System.out.println("  Actual Result : " + result);
          System.out.println();
     } 

      // write your codes here
     public static String verify(String traversal1, String traversal2, String traversal3) {
          int[] t1 = Arrays.stream(traversal1.split("-"))
                                   .map(String::trim)
                                   .mapToInt(Integer::parseInt)
                                   .toArray();
          int[] t2 = Arrays.stream(traversal2.split("-"))
                                   .map(String::trim)
                                   .mapToInt(Integer::parseInt)
                                   .toArray();
          int[] t3 = Arrays.stream(traversal3.split("-"))
                                   .map(String::trim)
                                   .mapToInt(Integer::parseInt)
                                   .toArray();

          // Every traversal of the same tree has the same number of keys
          if (!(t1.length == t2.length && t2.length == t3.length)) {
               return "Invalid traversals";
          }

          // A. Assignments
          // Store positions into traversals, not the arrays themselves.
          // Each triple is (inorder position, preorder position, postorder position).
          // Example: { 2, 0, 1 } means traversals[2] is inorder, traversals[0] is
          // preorder and traversals[1] is postorder.
          // All three positions must differ, so there are exactly 3! = 6 triples.
          int[][] traversals = { t1, t2, t3 };
          int[][] assignments = { { 0, 1, 2 }, { 0, 2, 1 }, { 1, 0, 2 }, { 1, 2, 0 }, { 2, 0, 1 }, { 2, 1, 0 } };

          // B. Loop over assignments
          for (int[] a : assignments) {
               int[] inorder = traversals[a[0]];
               int[] preorder = traversals[a[1]];
               int[] postorder = traversals[a[2]];

               // Step 3. Sorted test: the inorder of a BST must be strictly ascending
               if (!IntStream.range(0, inorder.length - 1).allMatch(i -> inorder[i] < inorder[i + 1])) {
                    continue;
               }

               // Step 4. Root test: preorder starts with the root and postorder ends with it
               if (preorder[0] != postorder[postorder.length - 1]) {
                    continue;
               }

               // Step 5. Map each inorder key to its position for O(1) root lookups
               Map<Integer, Integer> idx = new HashMap<>();
               for (int i = 0; i < inorder.length; i++) {
                    idx.put(inorder[i], i);
               }

               // Step 6. Rebuild the tree from preorder + inorder, recording a postorder
               // D. The rebuild
               // Each frame is { preLo, inLo, inHi, emit }.
               // preLo is the start of this subtree's preorder slice.
               // inLo and inHi are the two ends of its inorder slice.
               // emit = 1 marks a root whose children are done, ready to append.
               // An explicit stack replaces recursion, so roots still reach the
               // output list after both children, which makes it a postorder.
               List<Integer> output = new ArrayList<>();
               Deque<int[]> stack = new ArrayDeque<>();
               stack.push(new int[] { 0, 0, inorder.length - 1, 0 });
               boolean valid = true;
               while (!stack.isEmpty()) {
                    int[] frame = stack.pop();
                    int preLo = frame[0], inLo = frame[1], inHi = frame[2];

                    // Both children are done: append the root
                    if (frame[3] == 1) {
                         output.add(preorder[preLo]);
                         continue;
                    }

                    // Base case: an empty inorder slice is an empty subtree
                    if (inLo > inHi) {
                         continue;
                    }

                    // The first key of the preorder slice is this subtree's root
                    int root = preorder[preLo];
                    Integer p = idx.get(root);

                    // Reject if the root is not a key at all, or if its inorder position lies
                    // outside the current slice. With a global map, existing is not enough.
                    // This catches cases like preorder 2-3-1 against inorder 1-2-3.
                    if (p == null || p < inLo || p > inHi) {
                         valid = false;
                         break;
                    }

                    // Keys left of p in the inorder slice form the left subtree
                    int leftSize = p - inLo;

                    // Push in reverse of processing order (stack is last in, first out):
                    // the root's emit frame goes first so it runs after both children.
                    // Right subtree: preorder starts after the root and the whole left subtree.
                    // Left subtree: preorder starts right after the root.
                    stack.push(new int[] { preLo, inLo, inHi, 1 });
                    stack.push(new int[] { preLo + 1 + leftSize, p + 1, inHi, 0 });
                    stack.push(new int[] { preLo + 1, inLo, p - 1, 0 });
               }
               if (!valid) {
                    continue;
               }

               // Step 7. The recorded postorder must match the given postorder exactly
               if (!IntStream.range(0, postorder.length).allMatch(i -> output.get(i) == postorder[i])) {
                    continue;
               }

               // E. Output: label each input by its role, in input order (Traversal 1, 2, 3)
               String[] roles = new String[3];
               roles[a[0]] = "Inorder";
               roles[a[1]] = "Preorder";
               roles[a[2]] = "Postorder";
               return "Traversal 1 - " + roles[0] + ", Traversal 2 - " + roles[1] + ", Traversal 3 - " + roles[2];
          }

          // C. Step 8. No assignment succeeded
          return "Invalid traversals";
     }
}
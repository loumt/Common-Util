/**
 * Copyright (c) www.bugull.com
 */
package com.loumt.spring;

import java.io.Serializable;
import java.util.*;

/**
 * USED TO:
 * Log File:
 *
 * @author loumt(loumt@sanlogic.com)
 * @project Common-Util
 * @package com.loumt.spring
 * @date 2017/5/24/024
 */
public abstract class CollectionUtils {
    public CollectionUtils() {
    }

    public static boolean isEmpty(Collection collection) {
        return collection == null || collection.isEmpty();
    }

    public static boolean isEmpty(Map map) {
        return map == null || map.isEmpty();
    }

    public static List arrayToList(Object source) {
        return Arrays.asList(ObjectUtils.toObjectArray(source));
    }

    public static void mergeArrayIntoCollection(Object array, Collection collection) {
        if(collection == null) {
            throw new IllegalArgumentException("Collection must not be null");
        } else {
            Object[] arr = ObjectUtils.toObjectArray(array);
            Object[] var6 = arr;
            int var5 = arr.length;

            for(int var4 = 0; var4 < var5; ++var4) {
                Object elem = var6[var4];
                collection.add(elem);
            }

        }
    }

    public static void mergePropertiesIntoMap(Properties props, Map map) {
        if(map == null) {
            throw new IllegalArgumentException("Map must not be null");
        } else {
            String key;
            Object value;
            if(props != null) {
                for(Enumeration en = props.propertyNames(); en.hasMoreElements(); map.put(key, value)) {
                    key = (String)en.nextElement();
                    value = props.getProperty(key);
                    if(value == null) {
                        value = props.get(key);
                    }
                }
            }

        }
    }

    public static boolean contains(Iterator iterator, Object element) {
        if(iterator != null) {
            while(iterator.hasNext()) {
                Object candidate = iterator.next();
                if(ObjectUtils.nullSafeEquals(candidate, element)) {
                    return true;
                }
            }
        }

        return false;
    }

    public static boolean contains(Enumeration enumeration, Object element) {
        if(enumeration != null) {
            while(enumeration.hasMoreElements()) {
                Object candidate = enumeration.nextElement();
                if(ObjectUtils.nullSafeEquals(candidate, element)) {
                    return true;
                }
            }
        }

        return false;
    }

    public static boolean containsInstance(Collection collection, Object element) {
        if(collection != null) {
            Iterator var3 = collection.iterator();

            while(var3.hasNext()) {
                Object candidate = var3.next();
                if(candidate == element) {
                    return true;
                }
            }
        }

        return false;
    }

    public static boolean containsAny(Collection source, Collection candidates) {
        if(!isEmpty(source) && !isEmpty(candidates)) {
            Iterator var3 = candidates.iterator();

            while(var3.hasNext()) {
                Object candidate = var3.next();
                if(source.contains(candidate)) {
                    return true;
                }
            }

            return false;
        } else {
            return false;
        }
    }

    public static Object findFirstMatch(Collection source, Collection candidates) {
        if(!isEmpty(source) && !isEmpty(candidates)) {
            Iterator var3 = candidates.iterator();

            while(var3.hasNext()) {
                Object candidate = var3.next();
                if(source.contains(candidate)) {
                    return candidate;
                }
            }

            return null;
        } else {
            return null;
        }
    }



    public static boolean hasUniqueObject(Collection collection) {
        if(isEmpty(collection)) {
            return false;
        } else {
            boolean hasCandidate = false;
            Object candidate = null;
            Iterator var4 = collection.iterator();

            while(var4.hasNext()) {
                Object elem = var4.next();
                if(!hasCandidate) {
                    hasCandidate = true;
                    candidate = elem;
                } else if(candidate != elem) {
                    return false;
                }
            }

            return true;
        }
    }

    public static Class<?> findCommonElementType(Collection collection) {
        if(isEmpty(collection)) {
            return null;
        } else {
            Class candidate = null;
            Iterator var3 = collection.iterator();

            while(var3.hasNext()) {
                Object val = var3.next();
                if(val != null) {
                    if(candidate == null) {
                        candidate = val.getClass();
                    } else if(candidate != val.getClass()) {
                        return null;
                    }
                }
            }

            return candidate;
        }
    }



    private static class EnumerationIterator<E> implements Iterator<E> {
        private Enumeration<E> enumeration;

        public EnumerationIterator(Enumeration<E> enumeration) {
            this.enumeration = enumeration;
        }

        public boolean hasNext() {
            return this.enumeration.hasMoreElements();
        }

        public E next() {
            return this.enumeration.nextElement();
        }

        public void remove() throws UnsupportedOperationException {
            throw new UnsupportedOperationException("Not supported");
        }
    }

}

package org.csstudio.dct.util;

import java.util.ArrayList;
import java.util.List;

import org.csstudio.dct.model.IContainer;
import org.csstudio.dct.model.IElement;
import org.csstudio.dct.model.IFolder;
import org.csstudio.dct.model.internal.Folder;
import org.csstudio.dct.model.internal.Instance;
import org.csstudio.dct.model.internal.Prototype;
import org.csstudio.dct.model.internal.Record;

import com.google.common.base.Preconditions;

/**
 * Collection of utility methods that help comparing things.
 * 
 * @author Sven Wende
 * 
 */
public final class CompareUtil {
    private CompareUtil() {
    }

    /**
     * Check if two objects are equal.
     * 
     * @param o1
     *            Object 1
     * @param o2
     *            Object 2
     * @return true, if both Object equal
     */
    public static boolean equals(Object o1, Object o2) {
        boolean result = false;

        if (o1 == null) {
            if (o2 == null) {
                result = true;
            }
        } else {
            if (o1.equals(o2)) {
                result = true;
            }
        }

        return result;
    }

    /**
     * Check if the Id of the given objects are equal.
     * 
     * @param o1
     *            element 1
     * @param o2
     *            element 2
     * @return true, if the id's of both elements equal or both elements are
     *         null
     */
    public static boolean idsEqual(IElement o1, IElement o2) {
        boolean result = false;

        if (o1 != null) {
            if (o2 != null) {
                result = CompareUtil.equals(o1.getId(), o2.getId());
            }
        } else {
            if (o2 == null) {
                result = true;
            }
        }

        return result;
    }

    /**
     * Returns true, if the specified list contains only objects that are
     * compatible to the specified class type.
     * 
     * @param type
     *            the class type
     * @param elements
     *            the list of objects
     * 
     * @return true of the list contains only objects of a certain type
     */
    public static boolean containsOnly(Class<?> type, List<?> elements) {
        Preconditions.checkNotNull(elements);
        boolean result = true;
        for (Object e : elements) {
            result &= type.isAssignableFrom(e.getClass());
        }
        return result;
    }

    private interface IsFolder {
        boolean isFolder(IFolder folder);
    }

    /**
     * Returns true, if the selection contains an element that has the Library
     * folder as it's parent.
     * 
     * @param elements
     *            the list of objects
     * 
     * @return true of the list contains the Library folder
     */
    public static boolean childOfLibaryFolder(List<IElement> elements) {
        return childOfFolder(elements, new IsFolder() {
            @Override
            public boolean isFolder(IFolder folder) {
                return folder.isLibraryFolder();
            }
        });
    }

    /**
     * Returns true, if the selection contains an element that has the
     * prototypes folder as it's parent.
     * 
     * @param elements
     *            the list of objects
     * 
     * @return true of the list contains the Library folder
     */
    public static boolean childOfPrototypesFolder(List<IElement> elements) {
        return childOfFolder(elements, new IsFolder() {
            @Override
            public boolean isFolder(IFolder folder) {
                return folder.isPrototypesFolder();
            }
        });
    }

    /**
     * Returns true, if the selection contains an element that has the instances
     * folder as it's parent.
     * 
     * @param elements
     *            the list of objects
     * 
     * @return true of the list contains the Library folder
     */
    public static boolean childOfInstancesFolder(List<IElement> elements) {
        return childOfFolder(elements, new IsFolder() {
            @Override
            public boolean isFolder(IFolder folder) {
                return folder.isInstancesFolder();
            }
        });
    }

    /**
     * Returns true, if the selection contains an element that has the Library
     * folder as it's parent.
     * 
     * @param elements
     *            the list of objects
     * 
     * @return true of the list contains the Library folder
     */
    public static boolean childOfFolder(List<IElement> elements, IsFolder folder) {
        for (Object e : elements) {
            if (e instanceof Prototype) {
                Prototype prototype = (Prototype) e;
                if (prototype != null) {
                    if (!folder.isFolder(prototype.getRootFolder())) {
                        return false;
                    }
                }
            } else if (e instanceof Record) {
                Record record = (Record) e;
                IContainer rootContainer = record.getRootContainer();
                if (rootContainer instanceof Prototype) {
                    Prototype prototype = (Prototype) rootContainer;
                    if (!folder.isFolder(prototype.getRootFolder())) {
                        return false;
                    }
                } else if (rootContainer instanceof Instance) {
                    Instance instance = (Instance) rootContainer;
                    IFolder rootFolder = instance.getRootFolder();
                    if (!((rootFolder != null) && (folder.isFolder(rootFolder)))) {
                        return false;
                    }
                }
            } else if (e instanceof Instance) {
                Instance instance = (Instance) e;
                IFolder rootFolder = instance.getRootFolder();
                if (!((rootFolder != null) && (folder.isFolder(rootFolder)))) {
                    return false;
                }
            } else if (e instanceof IFolder) {
                IFolder currentFolder = (IFolder) e;
                IFolder rootFolder = currentFolder.getRootFolder();
                if (!((rootFolder != null) && (folder.isFolder(rootFolder)))) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Returns true, if the selection contains the Library folder.
     * 
     * @param elements
     *            the list of objects
     * 
     * @return true of the list contains the Library folder
     */
    public static boolean containsLibraryFolder(List<?> elements) {
        for (Object e : elements) {
            if (e instanceof Folder) {
                Folder folder = (Folder) e;
                if (folder.isLibraryFolder()) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Returns true, if the selection contains the Prototypes folder.
     * 
     * @param elements
     *            the list of objects
     * 
     * @return true of the list contains the Prototypes folder
     */
    public static boolean containsPrototypesFolder(List<?> elements) {
        for (Object e : elements) {
            if (e instanceof Folder) {
                Folder folder = (Folder) e;
                if (folder.isPrototypesFolder()) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Returns true, if the selection contains the Instances folder.
     * 
     * @param elements
     *            the list of objects
     * 
     * @return true of the list contains the Prototypes folder
     */
    public static boolean containsInstancesFolder(List<?> elements) {
        for (Object e : elements) {
            if (e instanceof Folder) {
                Folder folder = (Folder) e;
                if (folder.isInstancesFolder()) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Convert List entries to different type.
     * 
     * @param elements
     *            list of elements
     * 
     * @return list with converted elements
     */
    @SuppressWarnings("unchecked")
    public static <E> List<E> convert(List<?> elements) {
        Preconditions.checkNotNull(elements);
        List<E> result = new ArrayList<E>();
        for (Object o : elements) {
            result.add((E) o);
        }
        return result;
    }

}

# SelectionManager

[ ![Download](https://api.bintray.com/packages/ircover/selection-manager/core/images/download.svg?version=1.0.0) ](https://bintray.com/ircover/selection-manager/core/1.0.0/link)

Library to include all logic about selecting items of lists.

For example lets say we've got some class with `selection` dependency:

    class A(private val selectionManager: SelectionManager)
`SelectionManager` is interface, which has following default implementations:
- `MultipleSelection` - object allows you to select as many items as you want;
- `SingleSelection` - object allows you to select only one items from the list;
- `NoneSelection` - this object doesn't allow you any selection, but has same interface.

You can choose any of them or create your own implementation.

    val selection: SelectionManager = SingleSelection()    
    val a = A(selection)
Now you can listen for any changes of selected positions with method `registerSelectionChangeListener` (don't forget to hold the result of this method):
    
    val selectionDisposable =
        selection.registerSelectionChangeListener { position: Int, isSelected: Boolean -> 
            //whatever you need to do with selected position
        }
When this `SelectionManager` object is needed no more you should dispose your registration to avoid leaks.

    fun destroy() {
        selectionDisposable.dispose()
    }
Now you are ready to listen for changes. Selecting positions is as easy as calling one particular method - `clickPosition`:

    class A(private val selectionManager: SelectionManager) {
        fun onItemClick(position: Int) {
            selectionManager.clickPosition(position)
        }
    }
If you don't need to listen changes but want to get all selected positions in the end, you should call `getSelectedPositions` (all `SelectionManager` objects can do it):

    fun getSelectedPositions(): ArrayList<Int> = selection.getSelectedPositions()

## Interception

Sometimes you need to postpone selection changes until some action done, for example, loading data about selected item. Here you can use interception and used for that method `addSelectionInterceptor`:

    val interceptionDisposable =
        selection.addSelectionInterceptor { position: Int, isSelected: Boolean, callback: () -> Unit ->
            if(isSelected) {
                val selectedItem = items[position] //example of getting item by existing parameters
                val isDataLoadingSuccessful: Boolean = ...
                //download data for `selectedItem`
                if(isDataLoadingSuccessful) {
                    callback()
                }
            }
        }
In this way selection changes will never be applied while `isDataLoadingSuccessful` is `false`.

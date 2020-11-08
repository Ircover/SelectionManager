# SelectionManager

[ ![Download](https://api.bintray.com/packages/ircover/selection-manager/core/images/download.svg?version=1.1.0) ](https://bintray.com/ircover/selection-manager/core/1.1.0/link)

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

Sometimes you need to postpone selection changes until some action done, for example, loading data about selected item. Here you can use interception and used for that method `addSelectionInterceptor`, which is part of `InterceptableSelectionManager` interface:

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

## Data Source

In case you want to hold your items list with selected positions in one place, in version 1.1.0 was added `SelectableDataSource` class (and it's inheritor `InterceptableSelectableDataSource` to use interceptions). It's pretty easy to use it: put in constructor `SelectionManager` you want, starting items list is optional. After that it's the same as usage of `SelectionManager` itself, but there are some extra features:
    
You can change items list with different existing selection processing.

    usersDataSource.setDataSource(newUsers1, ChangeDataSourceMode.ClearAllSelection) //default one, in this case you can leave only the first parameter
    usersDataSource.setDataSource(newUsers2, ChangeDataSourceMode.HoldSelectedPositions)
    usersDataSource.setDataSource(newUsers3, ChangeDataSourceMode.HoldSelectedItems)

You can get selected items list, but not only positions of them.

    val selectedUsers: ArrayList<User> = usersDataSource.getSelectedItems()
    
As the previous one, listening for selected items works now not only for positions.

    val disposable = usersDataSource.registerItemSelectionChangeListener { item: User, isSelected: Boolean ->
        if(isSelected) { /* Do whatever you want with selected user */ }
    }
    
By the way, if you still need to work with items positions, `SelectableDataSource` class looking after selected indexes to exclude any `OutofBoundsException`s in your listeners.

#### Warning!

Make sure you are registering listener in `SelectableDataSource` object, not in `SelectionManager` you placed in its constructor. Otherwise you may still catch changing selection of item positions, that is out of items list size (after shortening items list).

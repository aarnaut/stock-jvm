package asset.customer.subject

/**
 * Observer to get notified when customer repository changes.
 */
interface CustomerObserver {

    /**
     * Customer repository has changed. New items can be retrieved.
     */
    fun onCustomersChanged()
}
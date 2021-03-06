package injection

import dagger.Module
import dagger.Provides
import repository.customer.ObservableCustomerRepository
import repository.customer.mock.ObservableCustomerRepositoryMock
import repository.invoice.InvoiceRepository
import repository.invoice.mock.InvoiceRepositoryMock
import repository.item.ObservableItemRepository
import repository.item.mock.ObservableItemRepositoryMock
import javax.inject.Singleton

@Module
internal class StorageModule {

    @Singleton
    @Provides
    fun provideCustomerRepository(): ObservableCustomerRepository {
        return ObservableCustomerRepositoryMock()
    }

    @Singleton
    @Provides
    fun provideSupplierRepository(): ObservableItemRepository {
        return ObservableItemRepositoryMock()
    }

    @Singleton
    @Provides
    fun provideInvoiceRepository(): InvoiceRepository {
        return InvoiceRepositoryMock()
    }
}
package com.nutrisport.data

import androidx.core.app.PendingIntentCompat.send

import com.nutrisport.data.domain.CustomerRepository
import com.sf.nutrisport.domain.Customer
import com.sf.nutrisport.util.RequestState
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.FirebaseUser
import dev.gitlive.firebase.auth.auth
import dev.gitlive.firebase.firestore.firestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.tasks.await

class CustomerRepositoryImpl: CustomerRepository {
    override suspend fun createCustomer(
        user: FirebaseUser?,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        try {
            if (user!=null){
                val customerCollection = Firebase.firestore.collection("customer")
                val customer = Customer(
                    id = user.uid,
                    firstName = user.displayName?.split(" ")?.firstOrNull()?: "Unknown",
                    lastName = user.displayName?.split(" ")?.lastOrNull()?: "Unknown",
                    email = user.email?:"Unknown"
                )

                val customerExist = customerCollection.document(customer.id).get().exists
                if (customerExist){
                    onSuccess()
                }else{
                    customerCollection.document(customer.id).set(customer)
                    onSuccess()
                }
            }else{
                onError("User is not available")
            }
        }catch (e: Exception){
            onError("Error while creating new customer")
        }
    }

    override fun getCurrentUserId(): String? {
        return Firebase.auth.currentUser?.uid
    }

    override suspend fun signOut(): RequestState<Unit> {
        return try{
            Firebase.auth.signOut()
            RequestState.Success(Unit)
        }catch (e:Exception){
            RequestState.Error("Error while signing out: ${e.message}")
        }
    }

    override fun readCustomerFlow(): Flow<RequestState<Customer>> = channelFlow {
        try{
            val userId = getCurrentUserId()
            if (userId!=null){
                val database = Firebase.firestore
                database.collection("customer")
                    .document(userId)
                    .snapshots
                    .collectLatest {document->
                        if (document.exists){
                            val customer = Customer(
                                id = document.id,
                                firstName = document.get("firstName"),
                                lastName = document.get("lastName"),
                                email = document.get("email"),
                                city = document.get("city"),
                                postalCode = document.get("postalCode"),
                                address = document.get("address"),
                                phoneNumber = document.get("phoneNumber"),
                                cart = document.get("cart")
                            )
                            send(RequestState.Success(customer))
                        }else{
                            send(RequestState.Error("Queried customer does not exist"))
                        }

                    }
            }else{
                send(RequestState.Error("User is not available"))
            }
        }catch (e: Exception){
            send(RequestState.Error("Error while reading customer information: ${e.message}"))
        }
    }
}
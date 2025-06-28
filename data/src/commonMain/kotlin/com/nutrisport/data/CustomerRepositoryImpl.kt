package com.nutrisport.data

import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import com.nutrisport.data.domain.CustomerRepository
import com.sf.nutrisport.domain.Customer
import com.sf.nutrisport.util.RequestState
import dev.gitlive.firebase.auth.FirebaseUser
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

                val customerExist = customerCollection.document(customer.id).get().await().exists()
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
}
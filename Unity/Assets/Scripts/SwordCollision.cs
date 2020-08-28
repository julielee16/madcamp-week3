using System.Collections;
using System.Collections.Generic;
using System.Security.Cryptography;
using UnityEngine;

public class SwordCollision : MonoBehaviour
{
    public GameObject player;
    public Collider sword;
    public float impactForce = 20f;
    public GameObject enemy;
        
    void OnTriggerEnter(Collider other)
    {
        if (other.gameObject.tag.Equals("Player") & !other.gameObject.name.Equals(player.name))
        {
            //Physics.IgnoreCollision(other, sword);
            //enemy.GetComponent<Rigidbody>().AddForce(transform.forward * impactForce);
            NupjookController nupjookController = enemy.GetComponent<NupjookController>();

            UnityEngine.Debug.Log("We hit the enemy!");

            if (player.GetComponent<NupjookController>().isAttack)
            {
                nupjookController.network.SendHit(nupjookController.myID);
                nupjookController.isDamaged = true;
                nupjookController.animator.SetTrigger("attacked");
            }
        }

        else
        {
            Physics.IgnoreCollision(other, sword);
            UnityEngine.Debug.Log("Ignored collision!");
        }
    }
    
}

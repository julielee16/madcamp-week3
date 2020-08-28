using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class FollowPlayer : MonoBehaviour
{
    public GameObject player;
    public float cameraDistance = 10.0f;

    // Use this for initialization
    void Start()
    {
    }

    void LateUpdate()
    {
        transform.position = player.transform.position - player.transform.forward * cameraDistance;
        transform.LookAt(player.transform.position);
        transform.position = new Vector3(transform.position.x + 0.1f, transform.position.y + 2, transform.position.z);
    }
}


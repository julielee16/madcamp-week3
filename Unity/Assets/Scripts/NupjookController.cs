using System;
using System.Collections;
using System.Collections.Generic;
using System.Collections.Specialized;
using System.ComponentModel;
using System.Configuration;
using System.IO;
using System.Text;
using System.Text.RegularExpressions;
using System.Threading;
using TMPro;
using UnityEngine;
using UnityEngine.SceneManagement;

public class NupjookController : MonoBehaviour
{
    public Transform cam;

    public float speed = 10f;
    public float turnSmoothTime = 0.1f;
    float rotateSpeed = 3f;
    float turnSmoothVelocity;
    public bool isAttack;

    public Animator animator;

    public BoxCollider collider;
    public Rigidbody rb;
    public float jumpForce = 1;
    public bool IsGrounded;
    public bool isDamaged;

    public NetworkController network;
    public string motion;
    public string id;
    public string myID;
    public string myNickname;

    public int maxHealth = 100;
    public int health;
    public HealthBar healthBar;
    public int specialGage;
    public bool defeated;
    public GameObject player;
    public GameObject enemy;
    public ScreenUI screenUI;
    public int numLose;
    public bool resetTimer;


    bool walking = false;
    float horizontal = 0f;
    float vertical = 0f;
    Vector3 direction, moveDir;
    float targetAngle, angle;
    bool check = false;

    public AudioSource audioSource;

    public AudioClip attack1;
    public AudioClip attack2;
    public AudioClip attack3;

    public AudioClip attacked1;
    public AudioClip attacked2;
    public AudioClip attacked3;

    public AudioClip bboy;
    public AudioClip crouch;
    public AudioClip dead;
    public AudioClip gangnam;
    public AudioClip jump;
    public AudioClip move;
    public AudioClip twist;


    void Start()
    {
        audioSource = GetComponent<AudioSource>();
        collider = GetComponent<BoxCollider>();
        network = GameObject.Find("Network").GetComponent<NetworkController>();
        screenUI = GameObject.Find("UI").GetComponent<ScreenUI>();
        health = maxHealth;
        healthBar.SetMaxHealth(maxHealth);
        specialGage = 0;
        defeated = false;
        isDamaged = false;
        walking = false;
        check = false;
        resetTimer = true;
    }

    private void Awake()
    {
        rb = GetComponent<Rigidbody>();
    }

    void Update()
    {
        motion = network.motion;
        id = network.ID;
        UnityEngine.Debug.Log(id);
        UnityEngine.Debug.Log(myID);

        UnityEngine.Debug.Log(walking);

        if (enemy.GetComponent<NupjookController>().defeated)
        {
            walking = false;
            animator.SetBool("walking", false);
            animator.SetTrigger("victory");
        }

        if (health <= 0)
        {
            animator.SetBool("walking", false);
            animator.SetTrigger("dying");
            defeated = true;
            audioSource.clip = dead;
            audioSource.Play();
            numLose += 1;
            if (screenUI.p1_Nickname.text.Equals(myNickname.Substring(1, myNickname.Length - 2)))
            {
                screenUI.p1_status.GetComponent<TextMeshProUGUI>().text = "Lose";
                screenUI.p2_status.GetComponent<TextMeshProUGUI>().text = "Victory";
            }
            else
            {
                screenUI.p2_status.GetComponent<TextMeshProUGUI>().text = "Lose";
                screenUI.p1_status.GetComponent<TextMeshProUGUI>().text = "Victory";
            }
            if (resetTimer)
            {
                screenUI.timeLeft = 5f;
                screenUI.p1_status.SetActive(false);
                screenUI.p2_status.SetActive(false);
                resetTimer = false;
            }

        }

        else
        {
            if (isDamaged & enemy.GetComponent<NupjookController>().check)
            {
                health -= 10;
                healthBar.SetHealth(health);
                isDamaged = false;
                enemy.GetComponent<NupjookController>().check = false;
            }

            if (id.Equals(myID))
            {
                Signal(motion);
                network.ResetMotion();
            }
        }
    }

    void Run(float h, float v)
    {
        audioSource.clip = move;
        audioSource.Play();
        UnityEngine.Debug.Log("Run" + h + v);

        direction = new Vector3(h, 0f, v).normalized;


        targetAngle = Mathf.Atan2(direction.x, direction.z) * Mathf.Rad2Deg + cam.eulerAngles.y;
        angle = Mathf.SmoothDampAngle(transform.eulerAngles.y, targetAngle, ref turnSmoothVelocity, turnSmoothTime);

        transform.rotation = Quaternion.Euler(0f, angle, 0f);

        moveDir = Quaternion.Euler(0f, targetAngle, 0f) * Vector3.forward;

        rb.MovePosition(transform.position + moveDir.normalized * speed * 5);

    }
    void Turn(float h, float v)
    {
        direction = new Vector3(h, 0f, v).normalized;
        targetAngle = Mathf.Atan2(direction.x, direction.z) * Mathf.Rad2Deg + cam.eulerAngles.y;
        angle = Mathf.SmoothDampAngle(transform.eulerAngles.y, targetAngle, ref turnSmoothVelocity, turnSmoothTime);

        transform.rotation = Quaternion.Euler(0f, angle, 0f);
        moveDir = Quaternion.Euler(0f, targetAngle, 0f) * Vector3.forward;

        Quaternion newRotation = Quaternion.LookRotation(moveDir);
        rb.rotation = Quaternion.Slerp(rb.rotation, newRotation, rotateSpeed * Time.deltaTime);
    }

    void Signal(string signal)
    {
        //isAttack = false;
        if (signal.Length > 1)
        {
            signal = signal.Substring(1, signal.Length - 2);
        }

        if (signal.Equals("KillSelf"))
        {
            animator.SetBool("walking", false);
            animator.SetTrigger("kill self");
            walking = false;
            isAttack = false;
            health -= 5;
            healthBar.SetHealth(health);
            audioSource.clip = attacked1;
            audioSource.Play();
        }

        else if (signal.Equals("Sting"))
        {
            animator.SetBool("walking", false);
            animator.SetTrigger("sting");
            walking = false;
            isAttack = true;
            check = true;
        }

        else if (signal.Equals("LeftToRightMiddle"))
        {
            animator.SetBool("walking", false);
            animator.SetTrigger("left to right middle");
            walking = false;
            isAttack = true;
            check = true;
            audioSource.clip = attack1;
            audioSource.Play();
        }

        else if (signal.Equals("RightToLeftMiddle"))
        {
            animator.SetBool("walking", false);
            animator.SetTrigger("right to left middle");
            walking = false;
            isAttack = true;
            check = true;
            audioSource.clip = attack2;
            audioSource.Play();
        }

        else if (signal.Equals("LeftTopToRightBottom"))
        {
            animator.SetBool("walking", false);
            animator.SetTrigger("left top to right bottom");
            walking = false;
            isAttack = true;
            check = true;
            audioSource.clip = attack3;
            audioSource.Play();
        }

        else if (signal.Equals("LeftBottomToRightTop"))
        {
            animator.SetBool("walking", false);
            animator.SetTrigger("left bottom to right top");
            walking = false;
            isAttack = true;
            check = true;
            audioSource.clip = attack1;
            audioSource.Play();
        }

        else if (signal.Equals("RightTopToLeftBottom"))
        {
            animator.SetBool("walking", false);
            animator.SetTrigger("right top to left bottom");
            walking = false;
            isAttack = true;
            check = true;
            audioSource.clip = attack2;
            audioSource.Play();
        }

        else if (signal.Equals("TopToBottom"))
        {
            animator.SetBool("walking", false);
            animator.SetTrigger("top to bottom defend");
            walking = false;
            isAttack = false;
            audioSource.clip = attacked2;
            audioSource.Play();
        }

        else if (signal.Equals("BottomToTop"))
        {
            animator.SetBool("walking", false);
            animator.SetTrigger("bottom to top defend");
            walking = false;
            isAttack = false;
            audioSource.clip = attacked3;
            audioSource.Play();
        }

        else if (signal.Equals("Jump"))
        {
            animator.SetBool("walking", false);
            Jump();
            walking = false;
            isAttack = false;
            audioSource.clip = jump;
            audioSource.Play();
        }

        else if (signal.Equals("Crouch"))
        {
            animator.SetBool("walking", false);
            animator.SetTrigger("crouch");
            walking = false;
            isAttack = false;
            audioSource.clip = crouch;
            audioSource.Play();
        }

        else if (signal.Equals("Gangnam"))
        {
            animator.SetBool("walking", false);
            animator.SetTrigger("gangnam");
            walking = false;
            isAttack = false;
            audioSource.clip = gangnam;
            audioSource.Play();
        }

        else if (signal.Equals("Breakdance"))
        {
            animator.SetBool("walking", false);
            animator.SetTrigger("breakdance");
            walking = false;
            isAttack = false;
            audioSource.clip = bboy;
            audioSource.Play();
        }

        else if (signal.Equals("Twist"))
        {
            animator.SetBool("walking", false);
            animator.SetTrigger("twist dance");
            walking = false;
            isAttack = false;
            audioSource.clip = twist;
            audioSource.Play();
        }

        else if (signal.Equals("up"))
        {
            animator.SetBool("walking", true);
            walking = true;
            isAttack = false;

            horizontal = 0f;
            vertical = 1f;
            Run(horizontal, vertical);
            UnityEngine.Debug.Log("up_signal" + horizontal + vertical);
        }

        else if (signal.Equals("down"))
        {
            animator.SetBool("walking", true);
            walking = true;
            isAttack = false;



            horizontal = 0f;
            vertical = -1f;
            Run(horizontal, vertical);

            UnityEngine.Debug.Log("down_signal" + horizontal + vertical);
        }

        else if (signal.Equals("right"))
        {
            animator.SetBool("walking", false);
            walking = false;
            isAttack = false;

            horizontal = 0.6f;
            vertical = 0f;

            Turn(horizontal, vertical);
            UnityEngine.Debug.Log("right_signal" + horizontal + vertical);
        }

        else if (signal.Equals("left"))
        {
            animator.SetBool("walking", false);
            walking = false;
            isAttack = false;

            horizontal = -0.6f;
            vertical = 0f;

            Turn(horizontal, vertical);
            UnityEngine.Debug.Log("left_signal" + horizontal + vertical);
        }

        else
        {
            animator.SetBool("walking", false);
        }
    }

    void Jump()
    {
        if (IsGrounded)
        {
            animator.SetTrigger("jump");
            rb.AddForce(Vector3.up * jumpForce, ForceMode.Impulse);
            IsGrounded = false;
        }
    }

    public void Reset()
    {
        health = maxHealth;
        healthBar.SetMaxHealth(maxHealth);
        specialGage = 0;
        defeated = false;
        isDamaged = false;
        walking = false;
        check = false;
        resetTimer = true;
    }
}
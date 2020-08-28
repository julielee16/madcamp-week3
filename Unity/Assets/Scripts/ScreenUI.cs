using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using TMPro;
using UnityEngine.SceneManagement;

public class ScreenUI : MonoBehaviour
{
    public bool isLoaded;
    public GameObject p1_status;
    public GameObject p2_status;

    public NetworkController network;

    public GameObject ready;
    public GameObject battle;
    public GameObject loadingPage;
    public GameObject pausePage;
    
    public TextMeshProUGUI timer;
    public TextMeshProUGUI p1_Nickname;
    public TextMeshProUGUI p2_Nickname;
    
    public float readyTimeVisible = 5f;
    public float battleTimeVisible = 8f;
    public float timeLeft;

    public GameObject nupjook1;
    public GameObject nupjook2;

    public int stage;

    public AudioSource audioSource;

    public AudioClip round1;
    public AudioClip round2;
    public AudioClip round3;

    // Start is called before the first frame update
    void Start()
    {
        network = GameObject.Find("Network").GetComponent<NetworkController>();
        audioSource = GetComponent<AudioSource>();
        audioSource.clip = round1;
        audioSource.Play();
        stage = 1;
        isLoaded = false;
        ready.SetActive(true);
        battle.SetActive(false);
        pausePage.SetActive(false);
        p1_status.SetActive(false);
        p2_status.SetActive(false);
        timeLeft = 100f;
        timer.text = timeLeft.ToString();
    }

    void Update ()
    {
        if (isLoaded)
        {
            UnityEngine.Debug.Log("Outside loop: " + timeLeft);
            loadingPage.SetActive(false);
            readyTimeVisible -= Time.deltaTime;
            battleTimeVisible -= Time.deltaTime;
            if (readyTimeVisible < 0)
            {
                ready.SetActive(false);
            }

            if (battleTimeVisible < 3 & battleTimeVisible > 0)
            {
                battle.SetActive(true);
            }

            if (battleTimeVisible < 0)
            {
                battle.SetActive(false);
            }

            if (readyTimeVisible < 0 & battleTimeVisible < 0 & timeLeft > 0)
            {
                UnityEngine.Debug.Log("Inside loop: " + timeLeft);
                timeLeft -= Time.deltaTime;
                timer.text = timeLeft.ToString("F0");
                if (timeLeft <= 30)
                {
                    timer.color = new Color32(240, 31, 31, 255);
                }
            }

            if (timeLeft <= 0)
            {
                Reset();
            }
        }
    }

    public void Pause ()
    {
        pausePage.SetActive(true);
        Time.timeScale = 0f;
    }

    public void Resume ()
    {
        pausePage.SetActive(false);
        Time.timeScale = 1f;
    }

    public void Reset()
    {
        p1_status.SetActive(false);
        p2_status.SetActive(false);
        stage += 1;
        ready.SetActive(true);
        battle.SetActive(false);
        readyTimeVisible = 5f;
        battleTimeVisible = 8f;
        timeLeft = 100f;
        timer.color = new Color32(255, 255, 255, 255);
        nupjook1.GetComponent<NupjookController>().Reset();
        nupjook2.GetComponent<NupjookController>().Reset();
        
        if (stage == 2)
        {
            audioSource.Stop();
            audioSource.clip = round2;
            audioSource.Play();
            nupjook1.transform.position = GameObject.Find("Sideway Prefab (6)").transform.position + new Vector3(0f, 6f, 3f);
            nupjook2.transform.position = GameObject.Find("Sideway Prefab (2)").transform.position + new Vector3(0f, 6f, 0f);
        }
        else if (stage == 3)
        {
            audioSource.Stop();
            audioSource.clip = round3;
            audioSource.Play();
            nupjook1.transform.position = GameObject.Find("Stage 3").transform.position + new Vector3(10f, 6f, 10f);
            nupjook2.transform.position = GameObject.Find("Stage 3").transform.position + new Vector3(10f, 6f, 10f);

        }
        else if (stage == 4)
        {
            if (nupjook1.GetComponent<NupjookController>().numLose < nupjook2.GetComponent<NupjookController>().numLose)
            {
                network.SendWinnerLoser(nupjook2.GetComponent<NupjookController>().myID);
            }
            else
            {
                network.SendWinnerLoser(nupjook1.GetComponent<NupjookController>().myID);
            }

            audioSource.mute = true;
            SceneManager.LoadScene(0);
        }
        
    }
}

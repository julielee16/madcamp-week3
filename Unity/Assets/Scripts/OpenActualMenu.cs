using System.Collections;
using System.Collections.Generic;
using System.Diagnostics;
using UnityEngine;
using UnityEngine.SceneManagement;

public class OpenActualMenu : MonoBehaviour
{
    public GameObject Panel;

    public void OpenPanel()
    {
        if (Panel != null)
        {
            Animator animator = Panel.GetComponent<Animator>();
            if (animator != null)
            {
                float timePassed = animator.GetFloat("timePassed");
                for(int i = 0; i < 10 * Time.deltaTime; i++)
                {
                    UnityEngine.Debug.Log("time passed");
                    timePassed += 1;
                    animator.SetFloat("timePassed", timePassed);
                }
            }
        }
    }

    public void PlayGame ()
    {
        SceneManager.LoadScene(SceneManager.GetActiveScene().buildIndex + 1);
    }

    public void QuitGame ()
    {
        UnityEngine.Debug.Log("Quit");
        Application.Quit();
    }
}

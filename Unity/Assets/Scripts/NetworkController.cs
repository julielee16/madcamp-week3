using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using SocketIO;
using System.Diagnostics;
using Newtonsoft.Json;
using System.Runtime.InteropServices;

public class NetworkController : MonoBehaviour
{
    private SocketIOComponent socketIOComponent;
    public string motion;
    public string ID;

    public string p1_nickname;
    public string p2_nickname;
    public string p1_ID;
    public string p2_ID;
    public NupjookController nupjook1;
    public NupjookController nupjook2;
    public ScreenUI nupjookUI;

    private void Start()
    {
        socketIOComponent = GetComponent<SocketIOComponent>();
        nupjookUI = GameObject.Find("UI").GetComponent<ScreenUI>();
        ConnectToServer();
    }


    private void ConnectToServer()
    {
        socketIOComponent.Connect();
        socketIOComponent.On("match", OnMatch);
        socketIOComponent.On("move", OnMove);
        socketIOComponent.On("attack", OnAttack);
        socketIOComponent.On("defense", OnDefense);
        socketIOComponent.On("jump", OnJump);
        socketIOComponent.On("crouch", OnCrouch);
        socketIOComponent.On("random", OnRandom);
        socketIOComponent.On("pause", OnPause);
        socketIOComponent.On("resume", OnResume);
    }

    private void OnMatch (SocketIOEvent args)
    {
        UnityEngine.Debug.Log("match begins");
        //UnityEngine.Debug.Log(args.ToString());
        p1_nickname = args.data["P1_Nickname"].ToString();
        p2_nickname = args.data["P2_Nickname"].ToString();
        p1_ID = args.data["P1_ID"].ToString();
        p2_ID = args.data["P2_ID"].ToString();
        nupjook1 = GameObject.Find("Player1").GetComponent<NupjookController>();
        nupjook2 = GameObject.Find("Player2").GetComponent<NupjookController>();
        nupjook1.myNickname = p1_nickname;
        nupjook2.myNickname = p2_nickname;
        nupjook1.myID = p1_ID;
        nupjook2.myID = p2_ID;

        nupjookUI.p1_Nickname.text = p1_nickname.Substring(1, p1_nickname.Length - 2);
        nupjookUI.p2_Nickname.text = p2_nickname.Substring(1, p2_nickname.Length - 2);
        nupjookUI.isLoaded = true;
    }

    private void OnMove (SocketIOEvent args)
    {
        UnityEngine.Debug.Log("Received Move");
        motion = args.data["direction"].ToString();
        ID = args.data["ID"].ToString();
        UnityEngine.Debug.Log(motion);
        UnityEngine.Debug.Log(ID);

    }

    private void OnAttack(SocketIOEvent args)
    {
        UnityEngine.Debug.Log("Received Attack");
        motion = args.data["motion"].ToString();
        ID = args.data["ID"].ToString();
        UnityEngine.Debug.Log(motion);
        UnityEngine.Debug.Log(ID);

    }

    private void OnDefense(SocketIOEvent args)
    {
        UnityEngine.Debug.Log("Received Defense");
        motion = args.data["motion"].ToString();
        ID = args.data["ID"].ToString();
        UnityEngine.Debug.Log(motion);
        UnityEngine.Debug.Log(ID);

    }

    private void OnJump(SocketIOEvent args)
    {
        UnityEngine.Debug.Log("Received Jump");
        motion = "'Jump'";
        ID = args.data["ID"].ToString();
        UnityEngine.Debug.Log(motion);
        UnityEngine.Debug.Log(ID);

    }

    private void OnCrouch(SocketIOEvent args)
    {
        UnityEngine.Debug.Log("Received Crouch");
        motion = "'Crouch'";
        ID = args.data["ID"].ToString();
        UnityEngine.Debug.Log(motion);
        UnityEngine.Debug.Log(ID);
    }

    private void OnRandom(SocketIOEvent args)
    {
        UnityEngine.Debug.Log("Received Random");
        var randomNumber = UnityEngine.Random.Range(0, 3);
        if (randomNumber < 1)
        {
            motion = "'Gangnam'";
        }
        else if (randomNumber > 1)
        {
            motion = "'Twist'";
        }
        else
        {
            motion = "'Breakdance'";
        }
        ID = args.data["ID"].ToString();
        UnityEngine.Debug.Log(motion);
        UnityEngine.Debug.Log(ID);
    }

    private void OnPause(SocketIOEvent args)
    {
        nupjookUI.Pause();
    }

    private void OnResume(SocketIOEvent args)
    {
        nupjookUI.Resume();
    }

    public void ResetMotion()
    {
        //UnityEngine.Debug.Log("Resetting");
        motion = "";
    }

    public void SendHit(string hitPlayerID)
    {
        var dataToSend = new JSONObject();
        UnityEngine.Debug.Log(dataToSend);
        dataToSend["ID"] = JSONObject.CreateStringObject(hitPlayerID.Substring(1, hitPlayerID.Length - 2));
        socketIOComponent.Emit("hit", dataToSend);
    }

    public void SendWinnerLoser(string loserID)
    {
        if (loserID.Equals(p1_ID))
        {
            var dataToSend = new JSONObject();
            dataToSend["winner"] = JSONObject.CreateStringObject(p2_ID.Substring(1, p2_ID.Length - 2));
            dataToSend["loser"] = JSONObject.CreateStringObject(p1_ID.Substring(1, p1_ID.Length - 2));
            socketIOComponent.Emit("victory", dataToSend);
        }
        else
        {
            var dataToSend = new JSONObject();
            dataToSend["winner"] = JSONObject.CreateStringObject(p1_ID.Substring(1, p1_ID.Length - 2));
            dataToSend["loser"] = JSONObject.CreateStringObject(p2_ID.Substring(1, p2_ID.Length - 2));
            socketIOComponent.Emit("victory", dataToSend);
        }
    }
}
